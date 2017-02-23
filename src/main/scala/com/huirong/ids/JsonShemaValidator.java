//package com.huirong.ids;
//
//import com.fasterxml.jackson.databind.JsonNode;
//
//import java.io.IOException;
//import com.github.fge.jackson.JsonLoader;
//import com.github.fge.jsonschema.core.exceptions.ProcessingException;
//import com.github.fge.jsonschema.core.report.ProcessingReport;
//import com.github.fge.jsonschema.main.JsonSchema;
//import com.github.fge.jsonschema.main.JsonSchemaFactory;
//
///**
// * Created by huirong on 17-2-23.
// */
//
//public class JsonShemaValidator {
//    private final static JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
//    /**
//     * validate instance and Schema,here including two functions. as follows:
//     * first： the Draft v4 will check the syntax both of schema and instance.
//     * second： instance validation.
//     *
//     * @param mainSchema
//     * @param instance
//     * @return
//     * @throws IOException
//     * @throws ProcessingException
//     */
//    public static ProcessingReport validatorSchema(String mainSchema, String instance) throws IOException, ProcessingException {
//        JsonNode mainNode = JsonLoader.fromString(mainSchema);
//        JsonNode instanceNode = JsonLoader.fromString(instance);
//        JsonSchema schema = factory.getJsonSchema(mainNode);
//        ProcessingReport processingReport = schema.validate(instanceNode);
//        return processingReport;
//    }
//
//}
