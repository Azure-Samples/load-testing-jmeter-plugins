package com.microsoft.eventhubplugin;

import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Set;
import java.util.HashSet;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jmeter.testelement.TestStateListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.messaging.eventhubs.*;
import com.azure.messaging.eventhubs.models.CreateBatchOptions;
import com.azure.core.amqp.exception.*;

public class EventHubPlugin extends AbstractSampler implements TestStateListener {

    private static final Logger log = LoggerFactory.getLogger(EventHubPlugin.class);

    private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<>(
            Arrays.asList(
                    "org.apache.jmeter.config.gui.SimpleConfigGui"));

    public static final String LIQUID_TEMPLATE_FILENAME = "liquidTemplateFileName";
    public static final String EVENT_HUB_CONNECTION_VAR_NAME = "eventHubConnectionVarName";
    public static final String EVENT_HUB_NAME = "eventHubName";

    private MessageRenderer messageRenderer = null;
    private String templateFileName = null;

    public EventHubPlugin() {
        super();
    }

    public void setEventHubConnectionVarName(String eventHubConnectionVarName) {
        setProperty(new StringProperty(EVENT_HUB_CONNECTION_VAR_NAME, eventHubConnectionVarName));
    }

    public String getEventHubConnectionVarName() {
        return getPropertyAsString(EVENT_HUB_CONNECTION_VAR_NAME);
    }

    public void setEventHubName(String eventHubName) {
        setProperty(new StringProperty(EVENT_HUB_NAME, eventHubName));
    }

    public String getEventHubName() {
        return getPropertyAsString(EVENT_HUB_NAME);
    }

    public void setLiquidTemplateFileName(String liquidTemplateFileName) {
        setProperty(new StringProperty(LIQUID_TEMPLATE_FILENAME, liquidTemplateFileName));
    }

    public String getLiquidTemplateFileName() {
        if (templateFileName == null) {
            templateFileName = getPropertyAsString(LIQUID_TEMPLATE_FILENAME);
            messageRenderer = new MessageRenderer(templateFileName);
        }

        return templateFileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SampleResult sample(Entry e) {
        boolean isSuccessful = false;

        SampleResult res = new SampleResult();
        res.setSampleLabel(this.getName());

        String threadName = Thread.currentThread().getName();
        String responseMessage = "";
        String requestBody = "";
        long bytes = 0;
        long sentBytes = 0;

        EventHubProducerClient producer = null;
        EventHubClientBuilder producerBuilder = new EventHubClientBuilder();

        try {
            String connectionStringVarName = getEventHubConnectionVarName();
            requestBody = "EventHub Connection String Var Name: ".concat(connectionStringVarName);

            final String connectionString = System.getenv(connectionStringVarName);

            requestBody = requestBody.concat("\n")
                    .concat("EventHub Connection String: ").concat(connectionString);

            producerBuilder = producerBuilder.connectionString(connectionString, getEventHubName());

            producer = producerBuilder.buildProducerClient();

            // prepare a batch of events to send to the event hub
            CreateBatchOptions batchOptions = new CreateBatchOptions();
            EventDataBatch batch = producer.createBatch(batchOptions);
            int msgCount = 1;
            String liquidFileName = getLiquidTemplateFileName();
            requestBody = requestBody.concat("\n")
                    .concat("Liquid Template File: ").concat(liquidFileName);

            String msg = messageRenderer.Render();
            requestBody = requestBody.concat("\n\n")
                    .concat("[Event data #").concat(String.valueOf(msgCount)).concat("]\n")
                    .concat("Body: ").concat(msg);
            EventData eventData = new EventData(msg);

            batch.tryAdd(eventData);

            bytes = batch.getSizeInBytes();

            res.sampleStart(); // Start timing
            // send the batch of events to the event hub
            producer.send(batch);

            sentBytes = batch.getSizeInBytes();
            res.latencyEnd();

            res.setDataType(SampleResult.TEXT);

            responseMessage = "OK";
            isSuccessful = true;
            res.sampleEnd(); // End timing
        } catch (AmqpException ex) {
            log.info("Error calling {} sampler. ", threadName, ex);
            if (ex.isTransient()) {
                responseMessage = "A transient error occurred in ".concat(threadName)
                        .concat(" sampler. Please try again later.\n");
            }
            responseMessage = responseMessage.concat(ex.getMessage());
            res.setResponseData(ex.getMessage(), "UTF-8");
        } catch (Exception ex) {
            res.setResponseData(ex.toString(), "UTF-8");
            responseMessage = ex.getMessage();
            log.info("Error calling {} sampler. ", threadName, ex);
        } finally {
            if (producer != null) {
                producer.close();
            }
            res.setSamplerData(requestBody); // Request Body
            res.setBytes(bytes);
            res.setSentBytes(sentBytes);
            res.setResponseMessage(responseMessage);
        }

        res.setSuccessful(isSuccessful);
        return res;
    }

    @Override
    public void testStarted() {
        testStarted(""); // $NON-NLS-1$
    }

    @Override
    public void testEnded() {
        testEnded(""); // $NON-NLS-1$
    }

    @Override
    public void testStarted(String host) {
        // ignored
    }

    // Ensure any remaining contexts are closed
    @Override
    public void testEnded(String host) {

    }

    /**
     * @see org.apache.jmeter.samplers.AbstractSampler#applies(org.apache.jmeter.config.ConfigTestElement)
     */
    @Override
    public boolean applies(ConfigTestElement configElement) {
        String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
        return APPLIABLE_CONFIG_CLASSES.contains(guiClass);
    }
}