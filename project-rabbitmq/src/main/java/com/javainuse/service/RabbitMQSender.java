package com.javainuse.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javainuse.model.Employee;
import com.javainuse.model.FileSend;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

@Service
public class RabbitMQSender {
	
	@Autowired
	private AmqpTemplate amqpTemplate;
	
	@Value("${javainuse.rabbitmq.exchange}")
	private String exchange;
	
	@Value("${javainuse.rabbitmq.routingkey}")
	private String routingkey;	
	String kafkaTopic = "java_in_use_topic";
	
	@Value("${javainuse.rabbitmq.queue}")
	private String queue;
	
	public void send(Employee company) {
		amqpTemplate.convertAndSend(exchange, routingkey, company);
		System.out.println("Send msg = " + company);	    
	}
	
	public void send(FileSend fileSend) throws AmqpException {
		amqpTemplate.convertAndSend(exchange, routingkey, fileSend);
		System.out.println("Send msg = " + fileSend);	    
	}
	
	public List<FileSend> response() throws AmqpException, IOException, TimeoutException {
		List<FileSend> fileSendResponse= new ArrayList<>();
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
	    channel.queueDeclare(queue, false, false, false, null);
	    channel.basicQos(1);
	    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
	        String message = new String(delivery.getBody(), "UTF-8");
        	ObjectMapper mapper = new ObjectMapper();
        	String jsonInString = message.toString();
        	FileSend obj = mapper.readValue(jsonInString, FileSend.class);
        	fileSendResponse.add(obj); 
        	//System.out.println(" [x] Received '" + message + "'");
	        try {
	        	doWork(message); 	           
	        } finally {
	            //System.out.println(" [x] Done");
	            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
	        }
	    };
	    channel.basicConsume(queue, true, deliverCallback, consumerTag -> { });	  
		return fileSendResponse;
	}
	
	private static void doWork(String task) {
	    for (char ch : task.toCharArray()) {
	        if (ch == '.') {
	            try {
	                Thread.sleep(1000);
	            } catch (InterruptedException _ignored) {
	                Thread.currentThread().interrupt();
	            }
	        }
	    }
	  }
	
}