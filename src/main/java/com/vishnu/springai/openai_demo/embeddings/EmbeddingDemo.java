package com.vishnu.springai.openai_demo.embeddings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vishnu.springai.openai_demo.services.OpenAiService;

@Controller
public class EmbeddingDemo {

	@Autowired
	private OpenAiService service;

	@GetMapping("/showEmbedding")
	public String showEmbedDemo() {
		return "embedDemo";

	}

	@PostMapping("/embedding")
	public String embed(@RequestParam String text, Model model) {
		float[] response = service.embed(text);
		model.addAttribute("response", response);
		return "embedDemo";

	}

}