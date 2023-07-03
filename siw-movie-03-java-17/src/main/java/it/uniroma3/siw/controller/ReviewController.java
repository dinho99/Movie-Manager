package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import jakarta.validation.Valid;

@Controller
public class ReviewController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ReviewRepository reviewRepository;
    
    @GetMapping("/movie/{id}/formNewReview")
    public String formNewReview(@PathVariable("id") Long id, Model model) {
        model.addAttribute("movie", this.movieRepository.findById(id).get());
        model.addAttribute("review", new Review());
        return "formNewReview.html";
    }

    @PostMapping("/movie/{id}/formNewReview")
    public String newReview(@PathVariable("id") Long id, @Valid @ModelAttribute("review") Review review, Model model) {
        Movie movie = this.movieRepository.findById(id).get();

        review.setMovie(movie);
        movie.getReviews().add(review);
        this.reviewRepository.save(review);
        this.movieRepository.save(movie);
        model.addAttribute("review", review);
        model.addAttribute("movie", movie);
        return "movie.html";
    }

    @GetMapping("/movie/{id}/reviews")
    public String getReviews(@PathVariable("id") Long id, Model model) {
        Movie movie = this.movieRepository.findById(id).get();
        model.addAttribute("movie", movie);
        model.addAttribute("reviews", movie.getReviews());
        return "movieReviews.html";
    }

    @GetMapping("/admin/movie/{id}/reviews")
    public String getReviewsToDelete(@PathVariable("id") Long id, Model model) {
        Movie movie = this.movieRepository.findById(id).get();
        model.addAttribute("movie", movie);
        model.addAttribute("reviews", movie.getReviews());
        return "admin/movieReviews.html";
    }

    @GetMapping("/admin/deleteReviews")
	public String deleteReviews(Model model) {
		model.addAttribute("movies", this.movieRepository.findAll());
		return "admin/formDeleteReviews.html";
	}

    @GetMapping("/admin/deleteReviewFromMovie/{idM}/{idR}")
    public String deleteReviewFromMovie(@PathVariable("idM") Long idM, @PathVariable("idR") Long idR, Model model) {
        Movie movie = this.movieRepository.findById(idM).get();
        Review review = this.reviewRepository.findById(idR).get();

        movie.getReviews().remove(review); //rimuovi la recensione dalla lista delle recensioni del film
        review.setMovie(null);
        this.reviewRepository.delete(review);

        model.addAttribute("movie", movie);
        model.addAttribute("reviews", movie.getReviews());
        return "admin/movieReviews.html";
    }

}