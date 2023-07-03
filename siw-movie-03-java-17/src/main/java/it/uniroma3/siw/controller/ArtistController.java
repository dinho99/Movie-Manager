package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import java.io.FileOutputStream;
import java.io.IOException;

import it.uniroma3.siw.controller.validator.ArtistValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;

@Controller
public class ArtistController {
	
	@Autowired 
	private ArtistRepository artistRepository;

	@Autowired
	private ArtistValidator artistValidator;

	@GetMapping(value="/admin/formNewArtist")
	public String formNewArtist(Model model) {
		model.addAttribute("artist", new Artist());
		return "admin/formNewArtist.html";
	}
	
	@GetMapping(value="/admin/indexArtist")
	public String indexArtist() {
		return "admin/indexArtist.html";
	}
	
	@PostMapping("/admin/artist")
	public String newArtist(@ModelAttribute("artist") Artist artist, BindingResult bindingResult, Model model, @RequestParam("image") MultipartFile image) throws IOException {
		this.artistValidator.validate(artist, bindingResult);
		if (!bindingResult.hasErrors()) {
			String fileName = StringUtils.cleanPath(image.getOriginalFilename());
			String uploadDir = "src/main/resources/static/images/";
			String filePath = uploadDir + fileName;
			FileOutputStream fileOutputStream = new FileOutputStream(filePath);
			fileOutputStream.write(image.getBytes());
			fileOutputStream.close();
			String imageName = filePath.substring(25);
			artist.setUrlOfPicture(imageName);
			this.artistRepository.save(artist); 
			model.addAttribute("artist", artist);
			return "artist.html";
		} else {
			return "admin/formNewArtist.html"; 
		}
	}

	@GetMapping("/artist/{id}")
	public String getArtist(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artist", this.artistRepository.findById(id).get());
		return "artist.html";
	}

	@GetMapping("/artist")
	public String getArtists(Model model) {
		model.addAttribute("artists", this.artistRepository.findAll());
		return "artists.html";
	}

	@GetMapping("/admin/artist")
	public String getArtistsToModify(Model model) {
		model.addAttribute("artists", this.artistRepository.findAll());
		return "admin/artists.html";
	}

	@GetMapping("/admin/artist/{id}")
	public String modifyArtist(@PathVariable("id") Long id ,Model model) {
		model.addAttribute("artist", this.artistRepository.findById(id).get());
		return "admin/artist.html";
	}

	@GetMapping("/admin/deleteArtist/{id}")
	public String deleteArtist(@PathVariable("id") Long id, Model model) {
		Artist artist = this.artistRepository.findById(id).get();
		this.artistRepository.delete(artist);
		model.addAttribute("artists", this.artistRepository.findAll());
		return "admin/artists.html";
	}
}
