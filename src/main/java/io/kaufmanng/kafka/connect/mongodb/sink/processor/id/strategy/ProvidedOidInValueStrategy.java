package io.kaufmanng.kafka.connect.mongodb.sink.processor.id.strategy;

public class ProvidedOidInValueStrategy extends ProvidedOidStrategy {
  public ProvidedOidInValueStrategy() {
    super(ProvidedIn.VALUE);
  }
}
