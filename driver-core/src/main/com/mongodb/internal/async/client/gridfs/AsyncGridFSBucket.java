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

package com.mongodb.internal.async.client.gridfs;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.annotations.ThreadSafe;
import com.mongodb.client.gridfs.model.GridFSDownloadOptions;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.internal.async.SingleResultCallback;
import com.mongodb.internal.async.client.AsyncClientSession;
import org.bson.BsonValue;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

/**
 * Represents a GridFS Bucket
 */
@ThreadSafe
public interface AsyncGridFSBucket {

    /**
     * The bucket name.
     *
     * @return the bucket name
     */
    String getBucketName();

    /**
     * Sets the chunk size in bytes. Defaults to 255.
     *
     * @return the chunk size in bytes.
     */
    int getChunkSizeBytes();

    /**
     * Get the write concern for the GridFSBucket.
     *
     * @return the {@link com.mongodb.WriteConcern}
     */
    WriteConcern getWriteConcern();

    /**
     * Get the read preference for the GridFSBucket.
     *
     * @return the {@link com.mongodb.ReadPreference}
     */
    ReadPreference getReadPreference();

    /**
     * Get the read concern for the GridFSBucket.
     *
     * @return the {@link com.mongodb.ReadConcern}
     * @mongodb.server.release 3.2
     * @mongodb.driver.manual reference/readConcern/ Read Concern
     */
    ReadConcern getReadConcern();

    /**
     * Create a new GridFSBucket instance with a new chunk size in bytes.
     *
     * @param chunkSizeBytes the new chunk size in bytes.
     * @return a new GridFSBucket instance with the different chunk size in bytes
     */
    AsyncGridFSBucket withChunkSizeBytes(int chunkSizeBytes);

    /**
     * Create a new GridFSBucket instance with a different read preference.
     *
     * @param readPreference the new {@link ReadPreference} for the database
     * @return a new GridFSBucket instance with the different readPreference
     */
    AsyncGridFSBucket withReadPreference(ReadPreference readPreference);

    /**
     * Create a new GridFSBucket instance with a different write concern.
     *
     * @param writeConcern the new {@link WriteConcern} for the database
     * @return a new GridFSBucket instance with the different writeConcern
     */
    AsyncGridFSBucket withWriteConcern(WriteConcern writeConcern);

    /**
     * Create a new MongoDatabase instance with a different read concern.
     *
     * @param readConcern the new {@link ReadConcern} for the database
     * @return a new GridFSBucket instance with the different ReadConcern
     * @mongodb.server.release 3.2
     * @mongodb.driver.manual reference/readConcern/ Read Concern
     */
    AsyncGridFSBucket withReadConcern(ReadConcern readConcern);

    /**
     * Opens a AsyncOutputStream that the application can write the contents of the file to.
     * <p>
     * As the application writes the contents to the returned Stream, the contents are uploaded as chunks in the chunks collection. When
     * the application signals it is done writing the contents of the file by calling close on the returned Stream, a files collection
     * document is created in the files collection.
     * </p>
     *
     * @param filename the filename for the stream
     * @return the GridFSUploadStream that provides the ObjectId for the file to be uploaded and the Stream to which the
     * application will write the contents.
     */
    AsyncGridFSUploadStream openUploadStream(String filename);

    /**
     * Opens a AsyncOutputStream that the application can write the contents of the file to.
     * <p>
     * As the application writes the contents to the returned Stream, the contents are uploaded as chunks in the chunks collection. When
     * the application signals it is done writing the contents of the file by calling close on the returned Stream, a files collection
     * document is created in the files collection.
     * </p>
     *
     * @param filename the filename for the stream
     * @param options  the GridFSUploadOptions
     * @return the GridFSUploadStream that provides the ObjectId for the file to be uploaded and the Stream to which the
     * application will write the contents.
     */
    AsyncGridFSUploadStream openUploadStream(String filename, GridFSUploadOptions options);

    /**
     * Opens a AsyncOutputStream that the application can write the contents of the file to.
     * <p>
     * As the application writes the contents to the returned Stream, the contents are uploaded as chunks in the chunks collection. When
     * the application signals it is done writing the contents of the file by calling close on the returned Stream, a files collection
     * document is created in the files collection.
     * </p>
     *
     * @param id the custom id value of the file
     * @param filename the filename for the stream
     * @return the GridFSUploadStream that provides the ObjectId for the file to be uploaded and the Stream to which the
     * application will write the contents.
     */
    AsyncGridFSUploadStream openUploadStream(BsonValue id, String filename);

    /**
     * Opens a AsyncOutputStream that the application can write the contents of the file to.
     * <p>
     * As the application writes the contents to the returned Stream, the contents are uploaded as chunks in the chunks collection. When
     * the application signals it is done writing the contents of the file by calling close on the returned Stream, a files collection
     * document is created in the files collection.
     * </p>
     *
     * @param id the custom id value of the file
     * @param filename the filename for the stream
     * @param options  the GridFSUploadOptions
     * @return the GridFSUploadStream that provides the ObjectId for the file to be uploaded and the Stream to which the
     * application will write the contents.
     */
    AsyncGridFSUploadStream openUploadStream(BsonValue id, String filename, GridFSUploadOptions options);

    /**
     * Opens a AsyncOutputStream that the application can write the contents of the file to.
     * <p>
     * As the application writes the contents to the returned Stream, the contents are uploaded as chunks in the chunks collection. When
     * the application signals it is done writing the contents of the file by calling close on the returned Stream, a files collection
     * document is created in the files collection.
     * </p>
     *
     * @param clientSession the client session with which to associate this operation
     * @param filename the filename for the stream
     * @return the GridFSUploadStream that provides the ObjectId for the file to be uploaded and the Stream to which the
     * application will write the contents.
     * @since 3.6
     * @mongodb.server.release 3.6
     */
    AsyncGridFSUploadStream openUploadStream(AsyncClientSession clientSession, String filename);

    /**
     * Opens a AsyncOutputStream that the application can write the contents of the file to.
     * <p>
     * As the application writes the contents to the returned Stream, the contents are uploaded as chunks in the chunks collection. When
     * the application signals it is done writing the contents of the file by calling close on the returned Stream, a files collection
     * document is created in the files collection.
     * </p>
     *
     * @param clientSession the client session with which to associate this operation
     * @param filename the filename for the stream
     * @param options  the GridFSUploadOptions
     * @return the GridFSUploadStream that provides the ObjectId for the file to be uploaded and the Stream to which the
     * application will write the contents.
     * @since 3.6
     * @mongodb.server.release 3.6
     */
    AsyncGridFSUploadStream openUploadStream(AsyncClientSession clientSession, String filename, GridFSUploadOptions options);

    /**
     * Opens a AsyncOutputStream that the application can write the contents of the file to.
     * <p>
     * As the application writes the contents to the returned Stream, the contents are uploaded as chunks in the chunks collection. When
     * the application signals it is done writing the contents of the file by calling close on the returned Stream, a files collection
     * document is created in the files collection.
     * </p>
     *
     * @param clientSession the client session with which to associate this operation
     * @param id the custom id value of the file
     * @param filename the filename for the stream
     * @return the GridFSUploadStream that provides the ObjectId for the file to be uploaded and the Stream to which the
     * application will write the contents.
     * @since 3.6
     * @mongodb.server.release 3.6
     */
    AsyncGridFSUploadStream openUploadStream(AsyncClientSession clientSession, BsonValue id, String filename);

    /**
     * Opens a AsyncOutputStream that the application can write the contents of the file to.
     * <p>
     * As the application writes the contents to the returned Stream, the contents are uploaded as chunks in the chunks collection. When
     * the application signals it is done writing the contents of the file by calling close on the returned Stream, a files collection
     * document is created in the files collection.
     * </p>
     *
     * @param clientSession the client session with which to associate this operation
     * @param id the custom id value of the file
     * @param filename the filename for the stream
     * @param options  the GridFSUploadOptions
     * @return the GridFSUploadStream that provides the ObjectId for the file to be uploaded and the Stream to which the
     * application will write the contents.
     * @since 3.6
     * @mongodb.server.release 3.6
     */
    AsyncGridFSUploadStream openUploadStream(AsyncClientSession clientSession, BsonValue id, String filename, GridFSUploadOptions options);

    /**
     * Opens a AsyncInputStream from which the application can read the contents of the stored file specified by {@code id}.
     *
     * @param id the ObjectId of the file to be put into a stream.
     * @return the stream
     */
    AsyncGridFSDownloadStream openDownloadStream(ObjectId id);

    /**
     * Opens a AsyncInputStream from which the application can read the contents of the stored file specified by {@code id}.
     *
     * @param id the custom id value of the file, to be put into a stream.
     * @return the stream
     */
    AsyncGridFSDownloadStream openDownloadStream(BsonValue id);

    /**
     * Opens a Stream from which the application can read the contents of the latest version of the stored file specified by the
     * {@code filename}.
     *
     * @param filename the name of the file to be downloaded
     * @return the stream
     */
    AsyncGridFSDownloadStream openDownloadStream(String filename);

    /**
     * Opens a Stream from which the application can read the contents of the stored file specified by {@code filename} and the revision
     * in {@code options}.
     *
     * @param filename the name of the file to be downloaded
     * @param options  the download options
     * @return the stream
     */
    AsyncGridFSDownloadStream openDownloadStream(String filename, GridFSDownloadOptions options);

    /**
     * Opens a AsyncInputStream from which the application can read the contents of the stored file specified by {@code id}.
     *
     * @param clientSession the client session with which to associate this operation
     * @param id the ObjectId of the file to be put into a stream.
     * @return the stream
     * @since 3.6
     * @mongodb.server.release 3.6
     */
    AsyncGridFSDownloadStream openDownloadStream(AsyncClientSession clientSession, ObjectId id);

    /**
     * Opens a AsyncInputStream from which the application can read the contents of the stored file specified by {@code id}.
     *
     * @param clientSession the client session with which to associate this operation
     * @param id the custom id value of the file, to be put into a stream.
     * @return the stream
     * @since 3.6
     * @mongodb.server.release 3.6
     */
    AsyncGridFSDownloadStream openDownloadStream(AsyncClientSession clientSession, BsonValue id);

    /**
     * Opens a Stream from which the application can read the contents of the latest version of the stored file specified by the
     * {@code filename}.
     *
     * @param clientSession the client session with which to associate this operation
     * @param filename the name of the file to be downloaded
     * @return the stream
     * @since 3.6
     * @mongodb.server.release 3.6
     */
    AsyncGridFSDownloadStream openDownloadStream(AsyncClientSession clientSession, String filename);

    /**
     * Opens a Stream from which the application can read the contents of the stored file specified by {@code filename} and the revision
     * in {@code options}.
     *
     * @param clientSession the client session with which to associate this operation
     * @param filename the name of the file to be downloaded
     * @param options  the download options
     * @return the stream
     * @since 3.6
     * @mongodb.server.release 3.6
     */
    AsyncGridFSDownloadStream openDownloadStream(AsyncClientSession clientSession, String filename, GridFSDownloadOptions options);

    /**
     * Finds all documents in the files collection.
     *
     * @return the GridFS find iterable interface
     * @mongodb.driver.manual tutorial/query-documents/ Find
     */
    AsyncGridFSFindIterable find();

    /**
     * Finds all documents in the collection that match the filter.
     * <p>
     * Below is an example of filtering against the filename and some nested metadata that can also be stored along with the file data:
     * <pre>
     *  {@code
     *      Filters.and(Filters.eq("filename", "mongodb.png"), Filters.eq("metadata.contentType", "image/png"));
     *  }
     *  </pre>
     *
     * @param filter the query filter
     * @return the GridFS find iterable interface
     * @see com.mongodb.client.model.Filters
     */
    AsyncGridFSFindIterable find(Bson filter);

    /**
     * Finds all documents in the files collection.
     *
     * @param clientSession the client session with which to associate this operation
     * @return the GridFS find iterable interface
     * @mongodb.driver.manual tutorial/query-documents/ Find
     * @since 3.6
     * @mongodb.server.release 3.6
     */
    AsyncGridFSFindIterable find(AsyncClientSession clientSession);

    /**
     * Finds all documents in the collection that match the filter.
     * <p>
     * Below is an example of filtering against the filename and some nested metadata that can also be stored along with the file data:
     * <pre>
     *  {@code
     *      Filters.and(Filters.eq("filename", "mongodb.png"), Filters.eq("metadata.contentType", "image/png"));
     *  }
     *  </pre>
     *
     * @param clientSession the client session with which to associate this operation
     * @param filter the query filter
     * @return the GridFS find iterable interface
     * @see com.mongodb.client.model.Filters
     * @since 3.6
     * @mongodb.server.release 3.6
     */
    AsyncGridFSFindIterable find(AsyncClientSession clientSession, Bson filter);

    /**
     * Given a {@code id}, delete this stored file's files collection document and associated chunks from a GridFS bucket.
     *
     * @param id       the ObjectId of the file to be deleted
     * @param callback the callback that is completed once the file has been deleted
     */
    void delete(ObjectId id, SingleResultCallback<Void> callback);

    /**
     * Given a {@code id}, delete this stored file's files collection document and associated chunks from a GridFS bucket.
     *
     * @param id       the ObjectId of the file to be deleted
     * @param callback the callback that is completed once the file has been deleted
     */
    void delete(BsonValue id, SingleResultCallback<Void> callback);

    /**
     * Given a {@code id}, delete this stored file's files collection document and associated chunks from a GridFS bucket.
     *
     * @param clientSession the client session with which to associate this operation
     * @param id       the ObjectId of the file to be deleted
     * @param callback the callback that is completed once the file has been deleted
     * @since 3.6
     * @mongodb.server.release 3.6
     */
    void delete(AsyncClientSession clientSession, ObjectId id, SingleResultCallback<Void> callback);

    /**
     * Given a {@code id}, delete this stored file's files collection document and associated chunks from a GridFS bucket.
     *
     * @param clientSession the client session with which to associate this operation
     * @param id       the ObjectId of the file to be deleted
     * @param callback the callback that is completed once the file has been deleted
     * @since 3.6
     * @mongodb.server.release 3.6
     */
    void delete(AsyncClientSession clientSession, BsonValue id, SingleResultCallback<Void> callback);

    /**
     * Renames the stored file with the specified {@code id}.
     *
     * @param id          the id of the file in the files collection to rename
     * @param newFilename the new filename for the file
     * @param callback    the callback that is completed once the file has been renamed
     */
    void rename(ObjectId id, String newFilename, SingleResultCallback<Void> callback);

    /**
     * Renames the stored file with the specified {@code id}.
     *
     * @param id          the id of the file in the files collection to rename
     * @param newFilename the new filename for the file
     * @param callback    the callback that is completed once the file has been renamed
     */
    void rename(BsonValue id, String newFilename, SingleResultCallback<Void> callback);

    /**
     * Renames the stored file with the specified {@code id}.
     *
     * @param clientSession the client session with which to associate this operation
     * @param id          the id of the file in the files collection to rename
     * @param newFilename the new filename for the file
     * @param callback    the callback that is completed once the file has been renamed
     * @since 3.6
     * @mongodb.server.release 3.6
     */
    void rename(AsyncClientSession clientSession, ObjectId id, String newFilename, SingleResultCallback<Void> callback);

    /**
     * Renames the stored file with the specified {@code id}.
     *
     * @param clientSession the client session with which to associate this operation
     * @param id          the id of the file in the files collection to rename
     * @param newFilename the new filename for the file
     * @param callback    the callback that is completed once the file has been renamed
     * @since 3.6
     * @mongodb.server.release 3.6
     */
    void rename(AsyncClientSession clientSession, BsonValue id, String newFilename, SingleResultCallback<Void> callback);

    /**
     * Drops the data associated with this bucket from the database.
     *
     * @param callback the callback that is completed once the collection has been dropped
     */
    void drop(SingleResultCallback<Void> callback);

    /**
     * Drops the data associated with this bucket from the database.
     *
     * @param clientSession the client session with which to associate this operation
     * @param callback the callback that is completed once the collection has been dropped
     * @since 3.6
     * @mongodb.server.release 3.6
     */
    void drop(AsyncClientSession clientSession, SingleResultCallback<Void> callback);
}
