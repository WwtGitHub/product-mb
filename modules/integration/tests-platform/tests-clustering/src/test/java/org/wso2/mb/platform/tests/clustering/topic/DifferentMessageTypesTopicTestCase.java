/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mb.platform.tests.clustering.topic;


import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.axis2client.ConfigurationContextProvider;
import org.wso2.carbon.event.stub.internal.TopicManagerAdminServiceEventAdminExceptionException;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.operations.clients.TopicAdminClient;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.ClientConfigurationException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.clients.operations.utils.JMSMessageType;
import org.wso2.mb.platform.common.utils.MBPlatformBaseTest;
import org.xml.sax.SAXException;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This class includes test cases to test different types of messages (e.g. byte, map, object,
 * stream) which can be sent to a topic.
 */
public class DifferentMessageTypesTopicTestCase extends MBPlatformBaseTest {

    private AutomationContext automationContext;
    private TopicAdminClient topicAdminClient;

    /**
     * Prepare environment for tests.
     *
     * @throws XPathExpressionException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws LoginAuthenticationExceptionException
     * @throws IOException
     */
    @BeforeClass(alwaysRun = true)
    public void init()
            throws XPathExpressionException, URISyntaxException, SAXException, XMLStreamException,
                   LoginAuthenticationExceptionException, IOException {
        super.initCluster(TestUserMode.SUPER_TENANT_ADMIN);

        automationContext = getAutomationContextWithKey("mb002");

        topicAdminClient = new TopicAdminClient(automationContext.getContextUrls().getBackEndUrl(),
                                                super.login(automationContext), ConfigurationContextProvider.getInstance().getConfigurationContext());

    }

    /**
     * Publish byte messages to a topic in a single node and receive from the same node with one
     * subscriber
     *
     * @throws IOException
     * @throws JMSException
     * @throws org.wso2.mb.integration.common.clients.operations.utils.ClientConfigurationException
     * @throws XPathExpressionException
     * @throws NamingException
     */
    @Test(groups = "wso2.mb", description = "Single publisher single subscriber byte messages", enabled = true)
    public void testByteMessageSingleSubSinglePubTopic()
            throws IOException, JMSException, ClientConfigurationException, XPathExpressionException,
                   NamingException {

        this.runMessageTypeTestCase(JMSMessageType.BYTE, "byteTopic1");
    }

    /**
     * Publish map messages to a topic in a single node and receive from the same node with one
     * subscriber
     *
     * @throws IOException
     * @throws JMSException
     * @throws org.wso2.mb.integration.common.clients.operations.utils.ClientConfigurationException
     * @throws XPathExpressionException
     * @throws NamingException
     */
    @Test(groups = "wso2.mb", description = "Single publisher single subscriber map messages",
            enabled = true)
    public void testMapMessageSingleSubSinglePubTopic()
            throws IOException, JMSException, ClientConfigurationException, XPathExpressionException,
                   NamingException {
        this.runMessageTypeTestCase(JMSMessageType.MAP, "mapTopic1");
    }


    /**
     * Publish object messages to a topic in a single node and receive from the same node with one
     * subscriber
     *
     * @throws IOException
     * @throws JMSException
     * @throws org.wso2.mb.integration.common.clients.operations.utils.ClientConfigurationException
     * @throws XPathExpressionException
     * @throws NamingException
     */
    @Test(groups = "wso2.mb", description = "Single publisher single subscriber object messages",
            enabled = true)
    public void testObjectMessageSingleSubSinglePubTopic()
            throws IOException, JMSException, ClientConfigurationException, XPathExpressionException,
                   NamingException {
        this.runMessageTypeTestCase(JMSMessageType.OBJECT, "objectTopic1");
    }

    /**
     * Publish stream messages to a topic in a single node and receive from the same node with one
     * subscriber
     *
     * @throws IOException
     * @throws JMSException
     * @throws org.wso2.mb.integration.common.clients.operations.utils.ClientConfigurationException
     * @throws XPathExpressionException
     * @throws NamingException
     */
    @Test(groups = "wso2.mb", description = "Single publisher single subscriber stream messages",
            enabled = true)
    public void testStreamMessageSingleSubSinglePubTopic()
            throws IOException, JMSException, ClientConfigurationException, XPathExpressionException,
                   NamingException {
        this.runMessageTypeTestCase(JMSMessageType.STREAM, "streamTopic1");
    }

    /**
     * Publish stream messages to a topic in a single node and receive from the same node with one
     * subscriber
     *
     * @throws TopicManagerAdminServiceEventAdminExceptionException
     * @throws RemoteException
     */
    @AfterClass(alwaysRun = true)
    public void destroy()
            throws TopicManagerAdminServiceEventAdminExceptionException, RemoteException {

        topicAdminClient.removeTopic("byteTopic1");
        topicAdminClient.removeTopic("mapTopic1");
        topicAdminClient.removeTopic("objectTopic1");
        topicAdminClient.removeTopic("streamTopic1");
    }

    /**
     * Runs a topic send and receive test case
     *
     * @param messageType     The message type to be used when publishing
     * @param destinationName The destination name for sender and receiver
     * @throws XPathExpressionException
     * @throws org.wso2.mb.integration.common.clients.operations.utils.ClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     */
    private void runMessageTypeTestCase(JMSMessageType messageType, String destinationName)
            throws XPathExpressionException, ClientConfigurationException, NamingException, JMSException,
                   IOException {

        // Number of expected messages
        long expectedCount = 2000L;
        // Number of messages send
        long sendCount = 2000L;

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(automationContext.getInstance().getHosts().get("default"),
                                                                                                     Integer.parseInt(automationContext.getInstance().getPorts().get("amqp")),
                                                                                                     ExchangeType.TOPIC, destinationName);
        consumerConfig.setMaximumMessagesToReceived(expectedCount);
        consumerConfig.setPrintsPerMessageCount(expectedCount / 10L);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(automationContext.getInstance().getHosts().get("default"),
                                                                                                        Integer.parseInt(automationContext.getInstance().getPorts().get("amqp")),
                                                                                                        ExchangeType.TOPIC, destinationName);
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / 10L);
        publisherConfig.setJMSMessageType(messageType);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig);
        publisherClient.startClient();

        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed.");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), expectedCount, "Message receiving failed.");
    }
}
