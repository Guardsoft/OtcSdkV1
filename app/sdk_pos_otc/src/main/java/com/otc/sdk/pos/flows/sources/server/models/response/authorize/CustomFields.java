package com.otc.sdk.pos.flows.sources.server.models.response.authorize;

import com.google.gson.annotations.SerializedName;

public class CustomFields{

	@SerializedName("COUNTABLE")
	private String countable;

	@SerializedName("RELOAD_PARAMS")
	private String reloadparams;

	@SerializedName("RELOAD_KEYS")
	private String reloadKeys;

	@SerializedName("BRAND")
	private String brand;

	@SerializedName("TERMINAL")
	private String terminal;

	@SerializedName("CURRENCY")
	private String currency;

	@SerializedName("SPONSORED_ADDRESS")
	private String sponsoredAddress;


	@SerializedName("ROW_IDENTIFIER")
	private String rowIdentifier;

	@SerializedName("ID_RESOLUTOR")
	private String idResolutor;

	@SerializedName("SPONSORED_NAME")
	private String sponsoredName;

	@SerializedName("CARD")
	private String card;

	@SerializedName("MERCHANT")
	private String merchant;

	@SerializedName("SPONSORED_PHONE")
	private String sponsoredPhone;

	@SerializedName("ADQUIRENTE")
	private String adquirente;

	@SerializedName("PROCESS_CODE")
	private String processCode;

	@SerializedName("SPONSORED_ID")
	private String sponsoredId;

	@SerializedName("SPONSORED_MCCI")
	private String sponsored_Mcci;


	public String getCountable() {
		return countable;
	}

	public void setCountable(String countable) {
		this.countable = countable;
	}

	public String getReloadparams() {
		return reloadparams;
	}

	public void setReloadparams(String reloadparams) {
		this.reloadparams = reloadparams;
	}

	public String getReloadKeys() {
		return reloadKeys;
	}

	public void setReloadKeys(String reloadKeys) {
		this.reloadKeys = reloadKeys;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getSponsoredAddress() {
		return sponsoredAddress;
	}

	public void setSponsoredAddress(String sponsoredAddress) {
		this.sponsoredAddress = sponsoredAddress;
	}

	public String getRowIdentifier() {
		return rowIdentifier;
	}

	public void setRowIdentifier(String rowIdentifier) {
		this.rowIdentifier = rowIdentifier;
	}

	public String getIdResolutor() {
		return idResolutor;
	}

	public void setIdResolutor(String idResolutor) {
		this.idResolutor = idResolutor;
	}

	public String getSponsoredName() {
		return sponsoredName;
	}

	public void setSponsoredName(String sponsoredName) {
		this.sponsoredName = sponsoredName;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public String getSponsoredPhone() {
		return sponsoredPhone;
	}

	public void setSponsoredPhone(String sponsoredPhone) {
		this.sponsoredPhone = sponsoredPhone;
	}

	public String getAdquirente() {
		return adquirente;
	}

	public void setAdquirente(String adquirente) {
		this.adquirente = adquirente;
	}

	public String getProcessCode() {
		return processCode;
	}

	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}

	public String getSponsoredId() {
		return sponsoredId;
	}

	public void setSponsoredId(String sponsoredId) {
		this.sponsoredId = sponsoredId;
	}

	public String getSponsored_Mcci() {
		return sponsored_Mcci;
	}

	public void setSponsored_Mcci(String sponsored_Mcci) {
		this.sponsored_Mcci = sponsored_Mcci;
	}

	@Override
	public String toString() {
		return "CustomFields{" +
				"countable='" + countable + '\'' +
				", reloadparams='" + reloadparams + '\'' +
				", reloadKeys='" + reloadKeys + '\'' +
				", brand='" + brand + '\'' +
				", terminal='" + terminal + '\'' +
				", currency='" + currency + '\'' +
				", sponsoredAddress='" + sponsoredAddress + '\'' +
				", rowIdentifier='" + rowIdentifier + '\'' +
				", idResolutor='" + idResolutor + '\'' +
				", sponsoredName='" + sponsoredName + '\'' +
				", card='" + card + '\'' +
				", merchant='" + merchant + '\'' +
				", sponsoredPhone='" + sponsoredPhone + '\'' +
				", adquirente='" + adquirente + '\'' +
				", processCode='" + processCode + '\'' +
				", sponsoredId='" + sponsoredId + '\'' +
				", sponsored_Mcci='" + sponsored_Mcci + '\'' +
				'}';
	}
}