package com.example.readinglist.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.readinglist.model.AmazonConfiguration;
import com.example.readinglist.model.Book;
import com.example.readinglist.repository.ReadingListRepository;

//@Controller
//@RequestMapping("/")
public class ReadingListController {

	private AmazonConfiguration amazonConfiguration;
	private ReadingListRepository readingListRepository;
	
	@Autowired
	public ReadingListController(AmazonConfiguration amazonConfiguration, ReadingListRepository readingListRepository) {
		super();
		this.amazonConfiguration = amazonConfiguration;
		this.readingListRepository = readingListRepository;
	}

	@GetMapping(value = "/{reader}")
	public String readersBooks(@PathVariable("reader") String reader, Model model) {
		
		System.out.println("Message: "+ message);

		List<Book> readingList = readingListRepository.findByReader(reader);
		if (readingList != null) {
			model.addAttribute("books", readingList);
			model.addAttribute("reader", reader);
			model.addAttribute("amazonID", amazonConfiguration.getAssociateID());
		}
		return "readingList";
	}

	@PostMapping(value = "/{reader}")
	public String addToReadingList(@PathVariable("reader") String reader, Book book) {
		book.setReader(reader);
		readingListRepository.save(book);
		return "redirect:/{reader}";
	}
	
	@Value("${message}")
	private String message;
	
}
