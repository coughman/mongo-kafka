# ObjectId Id Strategy

This custom strategy implementation allows valid ObjectID strings in kafka messages to be converted to ObjectIDs when writing to MongoDB. Currently

For example, if the kafka message looks like this with the `_id` field in string:

```json
{"_id": "692755b56a7df7a5e89dc29f","id": 1, "email": "me@example.com", "name": "John Doe"}
```

The MongoDB doc will have the same field in ObjectId format:

```json
{"_id": ObjectId("692755b56a7df7a5e89dc29f"),"id": 1, "email": "me@example.com", "name": "John Doe"}
```

Tested with both Atlas and MongoDB community server.

# Sample Connector Configs with ObjectId Strategy

To use the ObjectId strategy, make sure you overwrite the default one and configure your sink connector to use the custom class:

```json
    "document.id.strategy.overwrite.existing": "true",
    "document.id.strategy": "io.kaufmanng.kafka.connect.mongodb.sink.processor.id.strategy.ProvidedOidInValueStrategy"
```

If you are deploying this custom connector in Confluent Cloud:

```json
{
  "collection": "users",
  "confluent.custom.schema.registry.auto": "true",
  "connection.uri": "mongodb+srv://<MONGODB-CREDENTIALS>@<atlas-endpoint>.mongodb.net/",
  "database": "inventory",
  "document.id.strategy": "io.kaufmanng.kafka.connect.mongodb.sink.processor.id.strategy.ProvidedOidInValueStrategy",
  "document.id.strategy.overwrite.existing": "true",
  "errors.deadletterqueue.topic.name": "users-dlq",
  "errors.tolerance": "all",
  "topics": "users",
  "value.converter": "io.confluent.connect.avro.AvroConverter"
}
```

Alternatively, if you use Confluent Platform or self managing the connector in your connect cluster:

```json
{
    "name": "mongodb-sink-users",
    "config": {
        "collection": "users",
        "connection.uri": "mongodb://myuser:mypassword@localhost:27017",
        "connector.class": "com.mongodb.kafka.connect.MongoSinkConnector",
        "database": "inventory",
        "tasks.max": "1",
        "topics": "users2",
        "errors.tolerance": "all",
        "errors.deadletterqueue.topic.name": "users2-dlq",
        "document.id.strategy.overwrite.existing": "true",
        "document.id.strategy": "io.kaufmanng.kafka.connect.mongodb.sink.processor.id.strategy.ProvidedOidInValueStrategy"
    }
}
```

# Limitations

* The string _must_ be of valid HEX format as per ObjectId spec, otherwise the writing of the record will fail (and may go to DLQ if configured to do so)
