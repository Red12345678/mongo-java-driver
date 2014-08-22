/*
 * Copyright (c) 2008-2014 MongoDB, Inc.
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

package com.mongodb.operation;

import com.mongodb.async.MongoFuture;
import com.mongodb.binding.AsyncWriteBinding;
import com.mongodb.binding.WriteBinding;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.codecs.BsonDocumentCodec;

import static com.mongodb.assertions.Assertions.notNull;
import static com.mongodb.operation.CommandOperationHelper.executeWrappedCommandProtocol;
import static com.mongodb.operation.CommandOperationHelper.executeWrappedCommandProtocolAsync;
import static com.mongodb.operation.DocumentHelper.putIfNotZero;
import static com.mongodb.operation.OperationHelper.ignoreResult;

public class CreateCollectionOperation implements AsyncWriteOperation<Void>, WriteOperation<Void> {
    private final String databaseName;
    private final CreateCollectionOptions options;

    public CreateCollectionOperation(final String databaseName, final CreateCollectionOptions options) {
        this.databaseName = notNull("databaseName", databaseName);
        this.options = notNull("options", options);
    }

    @Override
    public Void execute(final WriteBinding binding) {
        executeWrappedCommandProtocol(databaseName, asDocument(), new BsonDocumentCodec(), binding);
        return null;
    }

    @Override
    public MongoFuture<Void> executeAsync(final AsyncWriteBinding binding) {
        return ignoreResult(executeWrappedCommandProtocolAsync(databaseName, asDocument(), new BsonDocumentCodec(), binding));
    }

    private BsonDocument asDocument() {
        BsonDocument document = new BsonDocument("create", new BsonString(options.getCollectionName()));
        putIfNotZero(document, "size", options.getSizeInBytes());
        document.put("capped", BsonBoolean.valueOf(options.isCapped()));
        if (options.isCapped()) {
            document.put("autoIndexId", BsonBoolean.valueOf(options.isAutoIndex()));
            putIfNotZero(document, "max", options.getMaxDocuments());
        }
        return document;
    }
}
