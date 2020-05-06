package com.tistory.yangpajuice.rc.item;

public class ProductItem implements IDataItem {
	private int DisplayLargeClassificationCode; // = 20;
	private String DisplayItemID; // = "1903070002";
	private String DisplayItemName; // = "<우상> 얼리버드 1+1";
	private String ItemImageNm; // = "http://caching.lottecinema.co.kr//Media/WebAdmin/dc1218cd010a41e988473d3a92552c04.jpg";
	private String ItemImageAlt; // = "얼리버드1+1 예매권 <우상>";
	private int CombiItmDivCd; // = 40;
	private String TagList; // = "2,4";
	private int CurrentSellPrice; // = 20000;
	private int DiscountSellPrice; // = 10000;
	private String DcRatio; // = "50";
	private String UseRestrictionsDayName; // = "";
	private int RefundPassibleDayCount; // = 7;
	private int VipOnlyYN; // = 0;
	private int SoldoutYN; // = 1;
	private int MenuId; // = 3;
	private String DisplayDate; // = null;
	private String CustomerBuyRestrictionsName; // = null;
	private String SettleCode; // = null;
	private int DuplDcDivisionCode; // = 0;
	private String DuplDcDivisionName; // = null;
	private int PackageYN; // = 0;
	private int ConstitutionProductionDivCode; // = 0;
	private int SetTypeCode; // = 0;
	private String PackageConstitutionProduct; // = null;
	private String PayImg; // = null;
	private String PayImgAlt; // = null;
	private String DetailImg; // = null;
	private String DetailImgAlt; // = null;
	private int CinemaID; // = 0;
	private int SmartOrderYN; // = 0;
	private String DisplayItemDescription; // = null;
	private int EarlyBirdYN; // = 0;
	private int SortSequence; // = 0;
	private int SellTypeCode; // = 0;
	private String MarketingAreaType; // = null;
	private int PresentYN; // = 0;
	private int OptionTemplateCode; // = 0;
	private String DcTypeCode; // = null;
	@Override
	public String getKey() {
		return DisplayItemID;
	}
	public int getDisplayLargeClassificationCode() {
		return DisplayLargeClassificationCode;
	}
	public void setDisplayLargeClassificationCode(int displayLargeClassificationCode) {
		DisplayLargeClassificationCode = displayLargeClassificationCode;
	}
	public String getDisplayItemID() {
		return DisplayItemID;
	}
	public void setDisplayItemID(String displayItemID) {
		DisplayItemID = displayItemID;
	}
	public String getDisplayItemName() {
		return DisplayItemName;
	}
	public void setDisplayItemName(String displayItemName) {
		DisplayItemName = displayItemName;
	}
	public String getItemImageNm() {
		return ItemImageNm;
	}
	public void setItemImageNm(String itemImageNm) {
		ItemImageNm = itemImageNm;
	}
	public String getItemImageAlt() {
		return ItemImageAlt;
	}
	public void setItemImageAlt(String itemImageAlt) {
		ItemImageAlt = itemImageAlt;
	}
	public int getCombiItmDivCd() {
		return CombiItmDivCd;
	}
	public void setCombiItmDivCd(int combiItmDivCd) {
		CombiItmDivCd = combiItmDivCd;
	}
	public String getTagList() {
		return TagList;
	}
	public void setTagList(String tagList) {
		TagList = tagList;
	}
	public int getCurrentSellPrice() {
		return CurrentSellPrice;
	}
	public void setCurrentSellPrice(int currentSellPrice) {
		CurrentSellPrice = currentSellPrice;
	}
	public int getDiscountSellPrice() {
		return DiscountSellPrice;
	}
	public void setDiscountSellPrice(int discountSellPrice) {
		DiscountSellPrice = discountSellPrice;
	}
	public String getDcRatio() {
		return DcRatio;
	}
	public void setDcRatio(String dcRatio) {
		DcRatio = dcRatio;
	}
	public String getUseRestrictionsDayName() {
		return UseRestrictionsDayName;
	}
	public void setUseRestrictionsDayName(String useRestrictionsDayName) {
		UseRestrictionsDayName = useRestrictionsDayName;
	}
	public int getRefundPassibleDayCount() {
		return RefundPassibleDayCount;
	}
	public void setRefundPassibleDayCount(int refundPassibleDayCount) {
		RefundPassibleDayCount = refundPassibleDayCount;
	}
	public int getVipOnlyYN() {
		return VipOnlyYN;
	}
	public void setVipOnlyYN(int vipOnlyYN) {
		VipOnlyYN = vipOnlyYN;
	}
	public int getSoldoutYN() {
		return SoldoutYN;
	}
	public void setSoldoutYN(int soldoutYN) {
		SoldoutYN = soldoutYN;
	}
	public int getMenuId() {
		return MenuId;
	}
	public void setMenuId(int menuId) {
		MenuId = menuId;
	}
	public String getDisplayDate() {
		return DisplayDate;
	}
	public void setDisplayDate(String displayDate) {
		DisplayDate = displayDate;
	}
	public String getCustomerBuyRestrictionsName() {
		return CustomerBuyRestrictionsName;
	}
	public void setCustomerBuyRestrictionsName(String customerBuyRestrictionsName) {
		CustomerBuyRestrictionsName = customerBuyRestrictionsName;
	}
	public String getSettleCode() {
		return SettleCode;
	}
	public void setSettleCode(String settleCode) {
		SettleCode = settleCode;
	}
	public int getDuplDcDivisionCode() {
		return DuplDcDivisionCode;
	}
	public void setDuplDcDivisionCode(int duplDcDivisionCode) {
		DuplDcDivisionCode = duplDcDivisionCode;
	}
	public String getDuplDcDivisionName() {
		return DuplDcDivisionName;
	}
	public void setDuplDcDivisionName(String duplDcDivisionName) {
		DuplDcDivisionName = duplDcDivisionName;
	}
	public int getPackageYN() {
		return PackageYN;
	}
	public void setPackageYN(int packageYN) {
		PackageYN = packageYN;
	}
	public int getConstitutionProductionDivCode() {
		return ConstitutionProductionDivCode;
	}
	public void setConstitutionProductionDivCode(int constitutionProductionDivCode) {
		ConstitutionProductionDivCode = constitutionProductionDivCode;
	}
	public int getSetTypeCode() {
		return SetTypeCode;
	}
	public void setSetTypeCode(int setTypeCode) {
		SetTypeCode = setTypeCode;
	}
	public String getPackageConstitutionProduct() {
		return PackageConstitutionProduct;
	}
	public void setPackageConstitutionProduct(String packageConstitutionProduct) {
		PackageConstitutionProduct = packageConstitutionProduct;
	}
	public String getPayImg() {
		return PayImg;
	}
	public void setPayImg(String payImg) {
		PayImg = payImg;
	}
	public String getPayImgAlt() {
		return PayImgAlt;
	}
	public void setPayImgAlt(String payImgAlt) {
		PayImgAlt = payImgAlt;
	}
	public String getDetailImg() {
		return DetailImg;
	}
	public void setDetailImg(String detailImg) {
		DetailImg = detailImg;
	}
	public String getDetailImgAlt() {
		return DetailImgAlt;
	}
	public void setDetailImgAlt(String detailImgAlt) {
		DetailImgAlt = detailImgAlt;
	}
	public int getCinemaID() {
		return CinemaID;
	}
	public void setCinemaID(int cinemaID) {
		CinemaID = cinemaID;
	}
	public int getSmartOrderYN() {
		return SmartOrderYN;
	}
	public void setSmartOrderYN(int smartOrderYN) {
		SmartOrderYN = smartOrderYN;
	}
	public String getDisplayItemDescription() {
		return DisplayItemDescription;
	}
	public void setDisplayItemDescription(String displayItemDescription) {
		DisplayItemDescription = displayItemDescription;
	}
	public int getEarlyBirdYN() {
		return EarlyBirdYN;
	}
	public void setEarlyBirdYN(int earlyBirdYN) {
		EarlyBirdYN = earlyBirdYN;
	}
	public int getSortSequence() {
		return SortSequence;
	}
	public void setSortSequence(int sortSequence) {
		SortSequence = sortSequence;
	}
	public int getSellTypeCode() {
		return SellTypeCode;
	}
	public void setSellTypeCode(int sellTypeCode) {
		SellTypeCode = sellTypeCode;
	}
	public String getMarketingAreaType() {
		return MarketingAreaType;
	}
	public void setMarketingAreaType(String marketingAreaType) {
		MarketingAreaType = marketingAreaType;
	}
	public int getPresentYN() {
		return PresentYN;
	}
	public void setPresentYN(int presentYN) {
		PresentYN = presentYN;
	}
	public int getOptionTemplateCode() {
		return OptionTemplateCode;
	}
	public void setOptionTemplateCode(int optionTemplateCode) {
		OptionTemplateCode = optionTemplateCode;
	}
	public String getDcTypeCode() {
		return DcTypeCode;
	}
	public void setDcTypeCode(String dcTypeCode) {
		DcTypeCode = dcTypeCode;
	}
}
