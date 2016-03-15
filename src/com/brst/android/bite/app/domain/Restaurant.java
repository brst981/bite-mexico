package com.brst.android.bite.app.domain;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {

	private String name;
	private String id;
	private String description;
	private int rating;
	private String address;
	private String cuisine;
	private String offerAvailable;
	private String webUrl;
	private String image;
	private String people;
	private String zipcode;
	private String latitude;
	private String longitude;
	private String dayNoOffer;
	private String monthNoOffer;
	private String peoplePerBook;
	private String phoneNo;
	private String bookingNo;
	private String cellphone;
	private String offer_Period;
	
	private List<String> listWeek;
	private List<String> offerPeriod;
	private List<String> nonavailable;
	private ArrayList<String> offers;

	
	public ArrayList<String> getOffers() {
		return offers;
	}

	public void setOffers(ArrayList<String> offers) {
		this.offers = offers;
	}

	public Restaurant() {
		super();
		this.name = "";
		this.description = "";
		this.rating = 0;
		this.address = "";
		this.cuisine = "";
		this.offerAvailable = "No Offers";

		this.webUrl = "";
		this.image = "";
		this.people = "No Information";
		this.zipcode = "";
		this.dayNoOffer = "";
		this.monthNoOffer = "";
		this.cellphone = "";
		this.peoplePerBook = "0";

	}

	private List<Rating> ratings;

	private List<String> galleryImages;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCuisine() {
		return cuisine;
	}

	public void setCuisine(String cuisine) {
		this.cuisine = cuisine;
	}

	public String getOfferAvailable() {
		return offerAvailable;
	}

	public void setOfferAvailable(String offerAvailable) {
		this.offerAvailable = offerAvailable;
	}

	public List<String> getOfferPeriod() {
		return offerPeriod;
	}

	public void setOfferPeriod(List<String> offerPeriod) {
		this.offerPeriod = offerPeriod;
	}

	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getPeople() {
		return people;
	}

	public void setPeople(String people) {
		this.people = people;
	}

	public List<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}

	public List<String> getGalleryImages() {
		return galleryImages;
	}

	public void setGalleryImages(List<String> galleryImages) {
		this.galleryImages = galleryImages;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getDayNoOffer() {
		return dayNoOffer;
	}

	public void setDayNoOffer(String dayNoOffer) {
		this.dayNoOffer = dayNoOffer;
	}

	public String getMonthNoOffer() {
		return monthNoOffer;
	}

	public void setMonthNoOffer(String monthNoOffer) {
		this.monthNoOffer = monthNoOffer;
	}

	public String getPeoplePerBook() {
		return peoplePerBook;
	}

	public void setPeoplePerBook(String peoplePerBook) {
		this.peoplePerBook = peoplePerBook;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getBookingNo() {
		return bookingNo;
	}

	public void setBookingNo(String bookingNo) {
		this.bookingNo = bookingNo;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	public List<String> getListWeek() {
		return listWeek;
	}

	public void setListWeek(List<String> listWeek) {
		this.listWeek = listWeek;
	}

	public List<String> getNonavailable() {
		return nonavailable;
	}

	public void setNonavailable(List<String> nonavailable) {
		this.nonavailable = nonavailable;
	}
	
	public String getOffer_Period() {
		return offer_Period;
	}

	public void setOffer_Period(String offer_Period) {
		this.offer_Period = offer_Period;
	}

}
