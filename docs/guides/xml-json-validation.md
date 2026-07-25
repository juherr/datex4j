# XML, JSON, and validation

datex4j exposes immutable, thread-safe facades for XML, conformant DATEX II JSON, and structured XSD
validation. Create one configured instance and share it across the application.

## XML

`DatexXml` creates `DatexMarshaller` instances. The facade wraps DATEX II 3.x publications in a
`payload` root automatically and handles the DATEX II 2.x `d2LogicalModel` shape through the selected
version provider.

```java
DatexMarshaller marshaller = DatexXml.builder()
        .validating(true)
        .prettyPrint(true)
        .build();

byte[] xml = marshaller.write(publication);
SituationPublication restored =
        marshaller.read(xml, SituationPublication.class);
```

Validation on the marshaller stops conversion on schema failure. Use it at trusted application
boundaries where invalid input must fail immediately.

See the runnable
[DatexExample](../../examples/src/main/java/dev/juherr/datex4j/examples/DatexExample.java).

## JSON

`DatexJson` hides Jackson and honors the generated model's Jakarta XML Binding annotations.

```java
DatexJsonMapper mapper = DatexJson.builder()
        .prettyPrint(true)
        .build();

byte[] json = mapper.write(publication);
SituationPublication restored =
        mapper.read(json, SituationPublication.class);
```

The JSON codec is best-effort where the JSON representation cannot express the generated XML model
without loss. MessageContainer values receive the conformant `payload` and `exchangeInformation`
envelope; other types serialize directly.

See the runnable
[JsonExample](../../examples/src/main/java/dev/juherr/datex4j/examples/JsonExample.java) and the
[fixture limitations](../../datex4j-json/src/test/resources/datex-json/README.md).

## Structured validation

`DatexValidator` returns every warning and error instead of throwing on the first problem.

```java
DatexValidator validator = DatexValidator.forVersion(DatexVersion.V3_7);
ValidationResult result = validator.validate(xmlBytes);

if (!result.isValid()) {
    result.errors().forEach(System.out::println);
}
```

Create one validator per DATEX II version and reuse it; schema compilation is intentionally cached
inside the immutable instance.

See the runnable
[ValidationExample](../../examples/src/main/java/dev/juherr/datex4j/examples/ValidationExample.java).

## Choose failure behavior

| Need | API |
|---|---|
| Read or write XML and fail immediately on invalid data | `DatexXml.builder().validating(true)` |
| Read or write XML without XSD enforcement | `DatexXml.createMarshaller()` |
| Produce or consume DATEX II JSON | `DatexJson.createMapper()` |
| Report all XSD warnings and errors | `DatexValidator` |
