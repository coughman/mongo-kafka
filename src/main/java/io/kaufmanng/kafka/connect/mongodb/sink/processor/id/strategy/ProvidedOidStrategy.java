package io.kaufmanng.kafka.connect.mongodb.sink.processor.id.strategy;

import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.sink.SinkRecord;

import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.types.ObjectId;

import com.mongodb.kafka.connect.sink.converter.SinkDocument;
import com.mongodb.kafka.connect.sink.processor.id.strategy.ProvidedStrategy;

abstract class ProvidedOidStrategy extends ProvidedStrategy {
  ProvidedOidStrategy(final ProvidedIn where) {
    super(where);
  }

  public BsonValue generateId(final SinkDocument doc, final SinkRecord orig) {

    BsonValue id = super.generateId(doc, orig);

    if (!(id instanceof BsonString)) {
      throw new DataException("id is not a BSON string");
    }
    if (!ObjectId.isValid(id.asString().getValue())) {
      throw new DataException("not a valid HEX string");
    }

    return new BsonObjectId(new ObjectId(((BsonString) id).getValue()));
  }
}
