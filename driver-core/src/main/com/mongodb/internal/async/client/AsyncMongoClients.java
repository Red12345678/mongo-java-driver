/*
 * Copyright 2008-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.internal.async.client;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoDriverInformation;
import com.mongodb.MongoInternalException;
import com.mongodb.connection.AsynchronousSocketChannelStreamFactoryFactory;
import com.mongodb.connection.StreamFactory;
import com.mongodb.connection.StreamFactoryFactory;
import com.mongodb.connection.TlsChannelStreamFactoryFactory;
import com.mongodb.internal.connection.Cluster;
import com.mongodb.internal.connection.DefaultClusterFactory;
import com.mongodb.lang.Nullable;
import org.bson.codecs.configuration.CodecRegistry;

import java.io.Closeable;

import static com.mongodb.assertions.Assertions.notNull;
import static com.mongodb.internal.event.EventListenerHelper.getCommandListener;

/**
 * A factory for MongoClient instances.
 */
public final class AsyncMongoClients {

    /**
     * Creates a new client with the default connection string "mongodb://localhost".
     *
     * @return the client
     */
    public static AsyncMongoClient create() {
        return create(new ConnectionString("mongodb://localhost"));
    }

    /**
     * Create a new client with the given connection string as if by a call to {@link #create(ConnectionString)}.
     *
     * @param connectionString the connection
     * @return the client
     * @see #create(ConnectionString)
     */
    public static AsyncMongoClient create(final String connectionString) {
        return create(new ConnectionString(connectionString));
    }

    /**
     * Create a new client with the given connection string.
     * <p>
     * For each of the settings classed configurable via {@link MongoClientSettings}, the connection string is applied by
     * calling the {@code applyConnectionString} method on an instance of setting's builder class, building the setting, and adding it to
     * an instance of {@link MongoClientSettings.Builder}.
     * </p>
     *
     * @param connectionString the settings
     * @return the client
     *
     * @see MongoClientSettings.Builder
     * @see com.mongodb.connection.ClusterSettings.Builder#applyConnectionString(ConnectionString)
     * @see com.mongodb.connection.ConnectionPoolSettings.Builder#applyConnectionString(ConnectionString)
     * @see com.mongodb.connection.ServerSettings.Builder#applyConnectionString(ConnectionString)
     * @see com.mongodb.connection.SslSettings.Builder#applyConnectionString(ConnectionString)
     * @see com.mongodb.connection.SocketSettings.Builder#applyConnectionString(ConnectionString)
     */
    public static AsyncMongoClient create(final ConnectionString connectionString) {
        return create(connectionString, null);
    }

    /**
     * Create a new client with the given connection string.
     *
     * <p>Note: Intended for driver and library authors to associate extra driver metadata with the connections.</p>
     *
     * @param connectionString       the settings
     * @param mongoDriverInformation any driver information to associate with the MongoClient
     * @return the client
     * @see AsyncMongoClients#create(ConnectionString)
     */
    public static AsyncMongoClient create(final ConnectionString connectionString,
                                          @Nullable final MongoDriverInformation mongoDriverInformation) {

        return create(MongoClientSettings.builder().applyConnectionString(connectionString).build(), mongoDriverInformation);
    }

    /**
     * Create a new client with the given client settings.
     *
     * @param settings the settings
     * @return the client
     * @since 3.7
     */
    public static AsyncMongoClient create(final MongoClientSettings settings) {
        return create(settings, null);
    }

    /**
     * Creates a new client with the given client settings.
     *
     * <p>Note: Intended for driver and library authors to associate extra driver metadata with the connections.</p>
     *
     * @param settings               the settings
     * @param mongoDriverInformation any driver information to associate with the MongoClient
     * @return the client
     * @since 3.7
     */
    public static AsyncMongoClient create(final MongoClientSettings settings,
                                          @Nullable final MongoDriverInformation mongoDriverInformation) {
        if (settings.getStreamFactoryFactory() == null) {
            if (settings.getSslSettings().isEnabled()) {
                return createWithTlsChannel(settings, mongoDriverInformation);
            } else {
                return createWithAsynchronousSocketChannel(settings, mongoDriverInformation);
            }
        } else {
            return createMongoClient(settings, mongoDriverInformation, getStreamFactory(settings, false),
                    getStreamFactory(settings, true), null);
        }
    }

    private static AsyncMongoClient createMongoClient(final MongoClientSettings settings,
                                                      @Nullable final MongoDriverInformation mongoDriverInformation,
                                                      final StreamFactory streamFactory, final StreamFactory heartbeatStreamFactory,
                                                      @Nullable final Closeable externalResourceCloser) {
        return new AsyncMongoClientImpl(settings, createCluster(settings, mongoDriverInformation, streamFactory, heartbeatStreamFactory),
                externalResourceCloser);
    }

    private static Cluster createCluster(final MongoClientSettings settings,
                                         @Nullable final MongoDriverInformation mongoDriverInformation,
                                         final StreamFactory streamFactory, final StreamFactory heartbeatStreamFactory) {
        notNull("settings", settings);
        return new DefaultClusterFactory().createCluster(settings.getClusterSettings(), settings.getServerSettings(),
                settings.getConnectionPoolSettings(), streamFactory, heartbeatStreamFactory, settings.getCredential(),
                getCommandListener(settings.getCommandListeners()), settings.getApplicationName(), mongoDriverInformation,
                settings.getCompressorList());
    }

    /**
     * Gets the default codec registry.  It includes the following providers:
     *
     * <ul>
     * <li>{@link org.bson.codecs.ValueCodecProvider}</li>
     * <li>{@link org.bson.codecs.BsonValueCodecProvider}</li>
     * <li>{@link com.mongodb.DBRefCodecProvider}</li>
     * <li>{@link com.mongodb.DBObjectCodecProvider}</li>
     * <li>{@link org.bson.codecs.DocumentCodecProvider}</li>
     * <li>{@link org.bson.codecs.IterableCodecProvider}</li>
     * <li>{@link org.bson.codecs.MapCodecProvider}</li>
     * <li>{@link com.mongodb.client.model.geojson.codecs.GeoJsonCodecProvider}</li>
     * <li>{@link com.mongodb.client.gridfs.codecs.GridFSFileCodecProvider}</li>
     * <li>{@link org.bson.codecs.jsr310.Jsr310CodecProvider}</li>
     * <li>{@link org.bson.codecs.BsonCodecProvider}</li>
     * </ul>
     *
     * @return the default codec registry
     * @see MongoClientSettings#getCodecRegistry()
     * @since 3.1
     */
    public static CodecRegistry getDefaultCodecRegistry() {
        return MongoClientSettings.getDefaultCodecRegistry();
    }


    private static AsyncMongoClient createWithTlsChannel(final MongoClientSettings settings,
                                                         @Nullable final MongoDriverInformation mongoDriverInformation) {
        final TlsChannelStreamFactoryFactory streamFactoryFactory = new TlsChannelStreamFactoryFactory();
        StreamFactory streamFactory = streamFactoryFactory.create(settings.getSocketSettings(), settings.getSslSettings());
        StreamFactory heartbeatStreamFactory = streamFactoryFactory.create(settings.getHeartbeatSocketSettings(),
                settings.getSslSettings());
        return createMongoClient(settings, mongoDriverInformation, streamFactory, heartbeatStreamFactory,
                new Closeable() {
                    @Override
                    public void close() {
                        streamFactoryFactory.close();
                    }
                });
    }

    private static AsyncMongoClient createWithAsynchronousSocketChannel(final MongoClientSettings settings,
                                                                        @Nullable final MongoDriverInformation mongoDriverInformation) {
        StreamFactoryFactory streamFactoryFactory = AsynchronousSocketChannelStreamFactoryFactory.builder().build();
        StreamFactory streamFactory = streamFactoryFactory.create(settings.getSocketSettings(), settings.getSslSettings());
        StreamFactory heartbeatStreamFactory = streamFactoryFactory.create(settings.getHeartbeatSocketSettings(),
                settings.getSslSettings());
        return createMongoClient(settings, mongoDriverInformation, streamFactory, heartbeatStreamFactory, null);
    }

    private static StreamFactory getStreamFactory(final MongoClientSettings settings, final boolean isHeartbeat) {
        StreamFactoryFactory streamFactoryFactory = settings.getStreamFactoryFactory();
        if (streamFactoryFactory == null) {
            throw new MongoInternalException("should not happen");
        }
        return streamFactoryFactory.create(isHeartbeat ? settings.getHeartbeatSocketSettings() : settings.getSocketSettings(),
                settings.getSslSettings());
    }

    private AsyncMongoClients() {
    }
}
