package org.wso2.mb.integration.common.clients;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.mb.integration.common.clients.operations.queue.QueueMessageBrowser;
import org.wso2.mb.integration.common.clients.operations.queue.QueueMessageReceiver;
import org.wso2.mb.integration.common.clients.operations.queue.QueueMessageSender;
import org.wso2.mb.integration.common.clients.operations.topic.TopicMessagePublisher;
import org.wso2.mb.integration.common.clients.operations.topic.TopicMessageReceiver;
import org.wso2.mb.integration.common.clients.operations.utils.*;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtilsTemp;
import org.apache.log4j.Logger;
import org.wso2.mb.integration.common.clients.configurations.AndesClientConfiguration;

import javax.jms.JMSException;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AndesClientTemp {
    private final Log log = LogFactory.getLog(AndesClientTemp.class);

    private AtomicInteger queueMessageCounter = new AtomicInteger(0);
    private AtomicInteger topicMessageCounter = new AtomicInteger(0);
    public String filePathToWriteReceivedMessages = System.getProperty("framework.resource.location") + File.separator +"receivedMessages.txt";

    private String mode = "";

    private String hostInformation = "localhost:5673";
    private String destinations = "topic:myTopic";
    private String printNumberOfMessagesPerAsString = "1";
    private String isToPrintEachMessageAsString = "false";
    private String numOfSecondsToRunAsString = "200";
    private String messageCountAsString = "100";
    private String numberOfThreadsAsString = "1";
    private String parameters = "listener=true,ackMode=1,delayBetweenMsg=0,stopAfter=100";
    private String connectionString = "";

    private String analyticOperation = "";
    private String numberOfMessagesExpectedForAnalysis = "";

    private String username = "admin";
    private String password = "admin";

    private String messageType = "text";

    private List<QueueMessageReceiver> queueListeners = new ArrayList<QueueMessageReceiver>();
    private List<TopicMessageReceiver> topicListeners = new ArrayList<TopicMessageReceiver>();
    private List<QueueMessageSender> queueMessageSenders = new ArrayList<QueueMessageSender>();
    private List<TopicMessagePublisher> topicMessagePublishers = new ArrayList<TopicMessagePublisher>();

    public AndesClientTemp(String mode, String hostInformation, String destinations,
                           String printNumberOfMessagesPerAsString,
                           String isToPrintEachMessageAsString, String numOfSecondsToRunAsString,
                           String messageCountAsString,
                           String numberOfThreadsAsString, String parameters,
                           String connectionString, String username,
                           String password) {
        this(mode, hostInformation, destinations, printNumberOfMessagesPerAsString, isToPrintEachMessageAsString, numOfSecondsToRunAsString, messageCountAsString, numberOfThreadsAsString, parameters, connectionString);
        this.username = username;
        this.password = password;
        AndesClientUtilsTemp.initializePrintWriter(filePathToWriteReceivedMessages);
    }

    public AndesClientTemp(String mode, String hostInformation, String destinations,
                           String printNumberOfMessagesPerAsString,
                           String isToPrintEachMessageAsString, String numOfSecondsToRunAsString,
                           String messageCountAsString,
                           String numberOfThreadsAsString, String parameters,
                           String connectionString) {
        this.mode = mode;
        this.hostInformation = hostInformation;
        this.destinations = destinations;
        this.printNumberOfMessagesPerAsString = printNumberOfMessagesPerAsString;
        this.isToPrintEachMessageAsString = isToPrintEachMessageAsString;
        this.numOfSecondsToRunAsString = numOfSecondsToRunAsString;
        this.messageCountAsString = messageCountAsString;
        this.numberOfThreadsAsString = numberOfThreadsAsString;
        this.parameters = parameters;
        this.connectionString = connectionString;
        AndesClientUtilsTemp.initializePrintWriter(filePathToWriteReceivedMessages);
    }

    public AndesClientTemp(String mode, String analiticOperation,
                           String numberOfMessagesExpeactedForAnalysis) {
        this.mode = mode;
        this.analyticOperation = analiticOperation;
        this.numberOfMessagesExpectedForAnalysis = numberOfMessagesExpeactedForAnalysis;
        AndesClientUtilsTemp.initializePrintWriter(filePathToWriteReceivedMessages);
    }

    public void startWorking(){

        queueMessageCounter.set(0);
        topicMessageCounter.set(0);
        //mode: send/receive/browse/purge/analyse
        //hosts host1:port,host2:port;
        //destinations queue:q1,q2,q3|topic:t1,t2,t3;
        //printNumberOfMessagesPer 100
        //isToPrintEachMessage false
        //numOfSecondsToRun
        //count 1000;
        //numOfThreads 5;
        //params listener=true,durable=false,subscriptionID=sub1,file="",ackMode=AUTO,delayBetweenMsg=200,stopAfter=12,ackAfterEach=300,commitAfterEach=300,rollbackAfterEach=400,unsubscribeAfter=500 (all parameters are optional)
        //connectionString (optional)

        //String mode = "receive";

        if (mode.equals("send") || mode.equals("receive")) {


/*            String hostInformation ="localhost:5673";
            String destinations = "topic:myTopic";
            String printNumberOfMessagesPerAsString = "1";
            String isToPrintEachMessageAsString = "false";
            String numOfSecondsToRunAsString = "200";
            String messageCountAsString = "100";
            String numberOfThreadsAsString = "1";
            String parameters = "listener=true,ackMode=1,delayBetweenMsg=0,stopAfter=100";
            String connectionString = "";*/

            //print input information
            log.info("hosts==>" + hostInformation);
            log.info("destinations==>" + destinations);
            log.info("mode==>" + mode);
            log.info("number of seconds to run==>" + numOfSecondsToRunAsString);
            log.info("message count==>" + messageCountAsString);
            log.info("num of threads==>" + numberOfThreadsAsString);
            log.info("print message count per==>" + printNumberOfMessagesPerAsString);
            log.info("is to print message==>" + isToPrintEachMessageAsString);
            log.info("parameters==>" + parameters);
            log.info("connectionString(optional)==>" + connectionString);


            //decode the host information
            String[] hostsAndPorts = hostInformation.split(",");

            //decode destinations
            String[] destinationList = destinations.split("\\|");

            String[] queues = null;
            String[] topics = null;

            for (int count = 0; count < destinationList.length; count++) {
                String destinationString = destinationList[count];
                if (destinationString.startsWith("queue")) {
                    String queueString = destinationString.split(":")[1];
                    queues = queueString.split(",");

                } else if (destinationString.startsWith("topic")) {
                    String topicString = destinationString.split(":")[1];
                    topics = topicString.split(",");
                }
            }


            //get mode of operation
            String modeOfOperation = mode;

            //get numberOfSecondsToRun
            int numberOfSecondsToWaitForMessages = Integer.MAX_VALUE;
            if (numOfSecondsToRunAsString != null && !numOfSecondsToRunAsString.equals("")) {
                numberOfSecondsToWaitForMessages = Integer.parseInt(numOfSecondsToRunAsString);
            }

            //decode message count
            int messageCount = 1;
            if (messageCountAsString != null && !messageCountAsString.equals("")) {
                messageCount = Integer.parseInt(messageCountAsString);
            }
            if (topics == null || topics.length == 0) {
                topicMessageCounter.set(messageCount);
            }
            if (queues == null || queues.length == 0) {
                queueMessageCounter.set(messageCount);
            }

            //decode thread count
            int numberOfThreads = 1;
            if (numberOfThreadsAsString != null && !numberOfThreadsAsString.equals("")) {
                numberOfThreads = Integer.parseInt(numberOfThreadsAsString);
            }

            //decode how often we should print message count
            int printNumberOfMessagesPer = 1;
            if (printNumberOfMessagesPerAsString != null && !printNumberOfMessagesPerAsString.equals("")) {
                printNumberOfMessagesPer = Integer.parseInt(printNumberOfMessagesPerAsString);
            }

            //decode if we should print each message
            boolean isToPrintEachMessage = false;
            if (isToPrintEachMessageAsString != null && !isToPrintEachMessageAsString.equals("")) {
                isToPrintEachMessage = Boolean.parseBoolean(isToPrintEachMessageAsString);
            }




            //decode parameters
            boolean isToUseListerner = true;
            boolean isDurable = false;
            String subscriptionID = "";
            String filePath = null;
            //default AUTO_ACK
            int ackMode = 1;
            int delayBetWeenMessages = 0;
            int stopAfter = Integer.MAX_VALUE;
            int ackAfterEach = Integer.MAX_VALUE;
            int commitAfterEach = Integer.MAX_VALUE;
            int rollbackAfterEach = Integer.MAX_VALUE;
            int unsubscribeAfter = Integer.MAX_VALUE;
            Long jmsExpiration = 0L; // By Default according to JMS 1.1, message expiration is only activated if this
            // value is larger than 0.

            String[] parameterStrings = parameters.split(",");
            for (int count = 0; count < parameterStrings.length; count++) {
                String[] keyValues = parameterStrings[count].split("=");
                String key = keyValues[0];
                String value = keyValues[1];
                if (key.equals("")) {

                } else if (key.equals("listener")) {
                    isToUseListerner = Boolean.parseBoolean(value);
                } else if (key.equals("durable")) {
                    isDurable = Boolean.parseBoolean(value);
                } else if (key.equals("subscriptionID")) {
                    subscriptionID = value;
                } else if (key.equals("file")) {
                    filePath = value;
                } else if (key.equals("ackMode")) {
                    ackMode = Integer.parseInt(value);
                } else if (key.equals("delayBetweenMsg")) {
                    delayBetWeenMessages = Integer.parseInt(value);
                } else if (key.equals("stopAfter")) {
                    stopAfter = Integer.parseInt(value);
                } else if (key.equals("ackAfterEach")) {
                    ackAfterEach = Integer.parseInt(value);
                } else if (key.equals("commitAfterEach")) {
                    commitAfterEach = Integer.parseInt(value);
                } else if (key.equals("rollbackAfterEach")) {
                    rollbackAfterEach = Integer.parseInt(value);
                } else if (key.equals("unsubscribeAfter")) {
                    unsubscribeAfter = Integer.parseInt(value);
                } else if (key.equals("jmsExpiration")) {
                    if (!value.equals("")) {
                        jmsExpiration = Long.parseLong(value);
                    }
                }
            }

            //*************************************************************************************************8


            //according to information start threads
            if (modeOfOperation.equals("send")) {
                log.info("===============Sending Messages====================");
                for (int count = 0; count < numberOfThreads; count++) {
                    int hostIndex = count % hostsAndPorts.length;
                    String host = hostsAndPorts[hostIndex].split(":")[0];
                    String port = hostsAndPorts[hostIndex].split(":")[1];
                    if (queues != null && queues.length != 0) {
                        int queueIndex = count % queues.length;
                        String queue = queues[queueIndex];

                        //start a queue sender
                        QueueMessageSender queueMessageSender = new QueueMessageSender(connectionString, host, port, this.username, this.password,
                                queue, queueMessageCounter, messageCount, delayBetWeenMessages, filePath, printNumberOfMessagesPer, isToPrintEachMessage,jmsExpiration);
                        queueMessageSender.setTypeOfMessage(messageType);
                        queueMessageSenders.add(queueMessageSender);

                        new Thread(queueMessageSender).start();

                    }
                    if (topics != null && topics.length != 0) {
                        int topicIndex = count % topics.length;
                        String topic = topics[topicIndex];

                        //start a topic sender
                        TopicMessagePublisher topicMessagePublisher = new TopicMessagePublisher(connectionString, host, port, this.username, this.password,
                                topic, topicMessageCounter, messageCount, delayBetWeenMessages, filePath, printNumberOfMessagesPer, isToPrintEachMessage,jmsExpiration);
                        topicMessagePublishers.add(topicMessagePublisher);

                        new Thread(topicMessagePublisher).start();

                    }

                }

            } else if (modeOfOperation.equals("receive")) {

                log.info("===============Receiving Messages====================");

                File fileToWriteReceivedMessages = new File(filePathToWriteReceivedMessages);
                if (isToPrintEachMessage) {
                    try {
                        if (fileToWriteReceivedMessages.exists()) {
                            fileToWriteReceivedMessages.delete();
                        }
                        fileToWriteReceivedMessages.getParentFile().mkdirs();
                        fileToWriteReceivedMessages.createNewFile();
                    } catch (IOException e) {
                        log.info("Cannot create a file to append receive messages" + e);
                    }
                }
                for (int count = 0; count < numberOfThreads; count++) {
                    int hostIndex = count % hostsAndPorts.length;
                    String host = hostsAndPorts[hostIndex].split(":")[0];
                    String port = hostsAndPorts[hostIndex].split(":")[1];
                    if (queues != null && queues.length != 0) {
                        int queueIndex = count % queues.length;
                        String queue = queues[queueIndex];

                        //start a queue receiver
                        QueueMessageReceiver queueMessageReceiver = new QueueMessageReceiver
                                (connectionString, host, port, this.username, this.password, queue, ackMode, isToUseListerner, queueMessageCounter, delayBetWeenMessages,
                                        printNumberOfMessagesPer, isToPrintEachMessage, filePathToWriteReceivedMessages, stopAfter, ackAfterEach, commitAfterEach, rollbackAfterEach);
                        queueListeners.add(queueMessageReceiver);
                        new Thread(queueMessageReceiver).start();
                    }
                    if (topics != null && topics.length != 0) {
                        int topicIndex = count % topics.length;
                        String topic = topics[topicIndex];

                        //start a topic receiver
                        TopicMessageReceiver topicMessageReceiver = new TopicMessageReceiver(connectionString, host, port, this.username, this.password, topic, isDurable,
                                subscriptionID, ackMode, isToUseListerner, topicMessageCounter, delayBetWeenMessages, printNumberOfMessagesPer, isToPrintEachMessage, filePathToWriteReceivedMessages, stopAfter, unsubscribeAfter, ackAfterEach, commitAfterEach, rollbackAfterEach);
                        topicListeners.add(topicMessageReceiver);
                        new Thread(topicMessageReceiver).start();

                    }
                }

            } else {
                log.info("ERROR: Unknown mode of operation");
            }
        } else if (mode.equals("browse")) {

            /*mode browse
              host Name And Port localhost:5672
              destination myQueue
              print Number Of Messages Per 100
              is To Print Each Message false
              */
=======
import java.util.concurrent.atomic.AtomicLong;

public abstract class AndesClient{
    protected AndesClientConfiguration config;
    private static Logger log = Logger.getLogger(AndesClient.class);

    protected AtomicLong sentMessageCount;
    protected AtomicLong receivedMessageCount;
    protected AtomicLong firstMessagePublishTimestamp;
    protected AtomicLong lastMessagePublishTimestamp;
    protected AtomicLong firstMessageConsumedTimestamp;
    protected AtomicLong lastMessageConsumedTimestamp;
    /**
     * Total latency in milliseconds
     */
    protected AtomicLong totalLatency;

    protected AndesClient(AndesClientConfiguration config) throws NamingException {
        this.config = config;
        this.initialize();
    }

    protected void initialize() throws NamingException{
        log.info("Initializing Andes client");
        sentMessageCount = new AtomicLong();
        receivedMessageCount = new AtomicLong();
        firstMessagePublishTimestamp = new AtomicLong();
        lastMessagePublishTimestamp = new AtomicLong();
        firstMessageConsumedTimestamp = new AtomicLong();
        lastMessageConsumedTimestamp = new AtomicLong();
        totalLatency = new AtomicLong();
    }

    public abstract void startClient() throws JMSException, NamingException, IOException;
>>>>>>> fa6777ff1fc1f01a4b0c79e507c5902707d8e7c8:modules/integration/tests-common/admin-clients/src/main/java/org/wso2/mb/integration/common/clients/AndesClient.java

    public abstract void stopClient() throws JMSException;

<<<<<<< HEAD:modules/integration/tests-common/admin-clients/src/main/java/org/wso2/mb/integration/common/clients/AndesClientTemp.java
/*          String hostnameandPort =args[1];
            String destination = args[2];
            String printNumberOfMessagesPerAsString = args[3];
            String isToPrintEachMessageAsString = args[4];*/

            //print input information
            log.info("mode==>" + mode);
            log.info("host and port==>" + hostInformation);
            log.info("destination==>" + destinations);
            log.info("print message count per==>" + printNumberOfMessagesPerAsString);
            log.info("is to print message==>" + isToPrintEachMessageAsString);

            //decode host and port
            String hostName = hostInformation.split(":")[0];
            String port = hostInformation.split(":")[1];

            //decode how often we should print message count
            int printNumberOfMessagesPer = 1;
            if (printNumberOfMessagesPerAsString != null && !printNumberOfMessagesPerAsString.equals("")) {
                printNumberOfMessagesPer = Integer.parseInt(printNumberOfMessagesPerAsString);
            }

            //decode if we should print each message
            boolean isToPrintEachMessage = false;
            if (isToPrintEachMessageAsString != null && !isToPrintEachMessageAsString.equals("")) {
                isToPrintEachMessage = Boolean.parseBoolean(isToPrintEachMessageAsString);
            }

            int messageCount = browseQueue(hostName, port, this.username, this.password, destinations, printNumberOfMessagesPer, isToPrintEachMessage);

            log.info("Browser Message Count: " + messageCount);


        } else if (mode.equals("purge")) {

            log.info("===============Purging Messages====================");

            //only applies to queues
/*          String hostnameandPort =args[1];
            String destination = args[2];*/

            //print input information
            log.info("mode==>" + mode);
            log.info("host and port==>" + hostInformation);

            //decode host and port
            String hostName = hostInformation.split(":")[0];
            String port = hostInformation.split(":")[1];

            //browse and get the message count
            int messageCount = browseQueue(hostName, port, this.username, this.password, destinations, Integer.MAX_VALUE, false);

            //activate receiver for the queue(if specified more first one/ if specified more than one host get the first one)
            //start a queue receiver
            QueueMessageReceiver queueMessageReceiver = new QueueMessageReceiver
                    ("", hostName, port, this.username, this.password, destinations, 1, true, queueMessageCounter, 0,
                            Integer.MAX_VALUE, false, "", messageCount, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
            queueListeners.add(queueMessageReceiver);
            new Thread(queueMessageReceiver).start();

            topicMessageCounter.set(messageCount);

        } else if (mode.equals("analyse")) {

            /**
             * mode analyse
             *     printMessages/printDuplicates/printMissing/printSorted/checkOrder/clearFile
             */
            String operation = analyticOperation;
            String numberOfMessagesSentAsString = "";
            int numberOfMessagesSent = 0;

            if (operation.equals("printMissing")) {

                numberOfMessagesSentAsString = numberOfMessagesExpectedForAnalysis;
                numberOfMessagesSent = Integer.parseInt(numberOfMessagesSentAsString);
            }

            //print input information
            log.info("mode==>" + mode);
            log.info("operation==>" + operation);

            AndesClientOutputParser andesClientOutputParser = null;
            try {
                andesClientOutputParser = new AndesClientOutputParser(filePathToWriteReceivedMessages);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (operation.equals("printMessages")) {
                andesClientOutputParser.printMessagesMap();
            } else if (operation.equals("printDuplicates")) {
                andesClientOutputParser.printDuplicateMessages();
            } else if (operation.equals("printMissing")) {
                andesClientOutputParser.printMissingMessages(numberOfMessagesSent);
            } else if (operation.equals("printSorted")) {
                andesClientOutputParser.printMessagesSorted();
            } else if (operation.equals("checkOrder")) {
                log.info("MESSAGE ORDER PRESERVED: " + andesClientOutputParser.checkIfMessagesAreInOrder());
            } else if (operation.equals("clearFile")) {
                andesClientOutputParser.clearFile();
            } else {
                log.info("analyse operation not found...");
            }
=======
    public double getPublisherTPS() {
        if (0 == this.lastMessagePublishTimestamp.get() - this.firstMessagePublishTimestamp.get()) {
            return this.sentMessageCount.doubleValue() / (1D / 1000);
>>>>>>> fa6777ff1fc1f01a4b0c79e507c5902707d8e7c8:modules/integration/tests-common/admin-clients/src/main/java/org/wso2/mb/integration/common/clients/AndesClient.java
        } else {
            return this.sentMessageCount.doubleValue() / ((this.lastMessagePublishTimestamp.doubleValue() - this.firstMessagePublishTimestamp.doubleValue()) / 1000);
        }
    }

    public double getSubscriberTPS() {
        if (0 == this.lastMessageConsumedTimestamp.get() - this.firstMessageConsumedTimestamp.get()) {
            return this.receivedMessageCount.doubleValue() / (1D / 1000);
        } else {
            return this.receivedMessageCount.doubleValue() / ((this.lastMessageConsumedTimestamp.doubleValue() - this.firstMessageConsumedTimestamp.doubleValue()) / 1000);
        }
<<<<<<< HEAD:modules/integration/tests-common/admin-clients/src/main/java/org/wso2/mb/integration/common/clients/AndesClientTemp.java

    }

    public int getReceivedqueueMessagecount() {
        return queueMessageCounter.get();
    }

    public int getReceivedTopicMessagecount() {
        return topicMessageCounter.get();
    }

    public Map<Long, Integer> checkIfMessagesAreDuplicated() throws IOException {
        AndesClientUtilsTemp.flushPrintWriter();
        AndesClientOutputParser andesClientOutputParser = new AndesClientOutputParser(filePathToWriteReceivedMessages);
        return andesClientOutputParser.checkIfMessagesAreDuplicated();
    }

    public boolean checkIfMessagesAreInOrder() throws IOException {
        AndesClientOutputParser andesClientOutputParser = new AndesClientOutputParser(filePathToWriteReceivedMessages);
        return andesClientOutputParser.checkIfMessagesAreInOrder();
    }

    /**
     * This method return whether received messages are transacted
     *
     * @param operationOccurredIndex Index of the operated message most of the time last message
     * @return
     */
    public boolean transactedOperation(long operationOccurredIndex) throws IOException {
        AndesClientOutputParser andesClientOutputParser = new AndesClientOutputParser(filePathToWriteReceivedMessages);
        return andesClientOutputParser.transactedOperations(operationOccurredIndex);
    }

    /**
     * This method returns number of duplicate received messages
     *
     * @return duplicate message count
     */
    public long getTotalNumberOfDuplicates() throws IOException {
        AndesClientOutputParser andesClientOutputParser = new AndesClientOutputParser(filePathToWriteReceivedMessages);
        return andesClientOutputParser.numberDuplicatedMessages();
    }

    public List<QueueMessageReceiver> getQueueListeners() {
        return queueListeners;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
=======
    }

    public double getAverageLatency() {
        if (0 == this.receivedMessageCount.get()) {
            log.warn("No messages were received");
            return 0D;
        } else {
            return (this.totalLatency.doubleValue() / 1000) / this.receivedMessageCount.doubleValue();
        }
    }

    public long getSentMessageCount() {
        return sentMessageCount.get();
    }

    public long getReceivedMessageCount() {
        return receivedMessageCount.get();
    }

    public AndesClientConfiguration getConfig(){
        return config;
    }
>>>>>>> fa6777ff1fc1f01a4b0c79e507c5902707d8e7c8:modules/integration/tests-common/admin-clients/src/main/java/org/wso2/mb/integration/common/clients/AndesClient.java
}
