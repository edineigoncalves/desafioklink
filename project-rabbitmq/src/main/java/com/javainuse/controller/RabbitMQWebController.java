package com.javainuse.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.javainuse.model.Employee;
import com.javainuse.model.FileSend;
import com.javainuse.service.RabbitMQSender;

@RestController
@RequestMapping(value = "/javainuse-rabbitmq/")
public class RabbitMQWebController {

	@Autowired
	RabbitMQSender rabbitMQSender;

	@GetMapping(value = "/producer")
	public String producer(@RequestParam("empName") String empName,@RequestParam("empId") String empId) {	
	Employee emp=new Employee();
	emp.setEmpId(empId);
	emp.setEmpName(empName);
		rabbitMQSender.send(emp);
		return "Message sent to the RabbitMQ JavaInUse Successfully";
	}
	
	@GetMapping(value = "/desafioOne")
	public String desafioOne(@RequestParam("nome") String nome,@RequestParam("base") byte[] base) {	
		try {
		FileSend fileSend= new FileSend();
		fileSend.setName(nome);
		fileSend.setBase(base);
		rabbitMQSender.send(fileSend);
		return "Message sent to the RabbitMQ JavaInUse Successfully";
		} catch (AmqpException a) {
			return "Message sent to the RabbitMQ JavaInUse with error. "+a.getMessage();
		}
	}
	
	@GetMapping(value = "/desafioTwo")
	public List<String> desafioTwo(){	
		List<String> lsResponse=new ArrayList<>();
		try {
		List<FileSend> fileSendResponse=rabbitMQSender.response();
		for (FileSend fileSend : fileSendResponse) {
			lsResponse.add(decodeString(fileSend.getBase()));
		}
		  return lsResponse;
		} catch (IOException | TimeoutException a) {
			lsResponse = new ArrayList<>();
			lsResponse.add(a.getMessage());
			return lsResponse;
		}
	}
	
	  public static String decodeString(byte[] encodedString) throws UnsupportedEncodingException {
		String msgDecode  = new String(encodedString, "UTF-8");  
        byte[] bytes = Base64.getDecoder().decode(msgDecode);
        return new String(bytes);
	  }

}

