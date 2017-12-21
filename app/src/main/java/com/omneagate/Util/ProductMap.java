/**
 *
 */
package com.omneagate.Util;

import com.omneagate.DTO.Product;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ftuser
 *
 */
public class ProductMap {

	public static final Map<String, String> productKeyMap = new HashMap<String, String>();

	public static final Map<String, Product> productMap = new HashMap<String, Product>();

	public static final Map<String, String> productPriceMap = new HashMap<String, String>();

	public static final Map<String, String> productAllotMap = new HashMap<String, String>();

	public static final Map<String, String> productIssueReportMap = new HashMap<String, String>();

	public static final Map<String, String> productLastTransaction = new HashMap<String, String>();

	private static final boolean SHOW=true;
	private static final boolean HIDE=false;

	/*
	 * This is for Beneficiary
	 */
	public static final Map<String, String> productAllotedMap = new HashMap<String, String>();
	public static final Map<String, String> productBalanceMap = new HashMap<String, String>();

	private static final String ATTA_CODE = "101";
	private static final String CHILLI_CODE = "102";
	private static final String KEROSENE_CODE = "103";
	private static final String POIL_CODE = "104";
	private static final String RGDAL_CODE = "105";
	private static final String RICEAAP_CODE = "106";
	private static final String RICEAFSC_CODE = "107";
	private static final String RICEFSC_CODE = "108";
	private static final String SALT_CODE = "109";
	private static final String SUGAR_CODE = "110";
	private static final String TAMARIND_CODE = "111";
	private static final String TURMARIC_CODE = "112";
	private static final String WHEAT_CODE = "113";
	public static final String RICE_CODE = "114";

	static {

		productKeyMap.put("atta", ATTA_CODE);
		productKeyMap.put("chilli", CHILLI_CODE);
		productKeyMap.put("kerosene", KEROSENE_CODE);
		productKeyMap.put("poil", POIL_CODE);
		productKeyMap.put("rgdal", RGDAL_CODE);
		productKeyMap.put("riceAAP", RICEAAP_CODE);
		productKeyMap.put("riceAFSC", RICEAFSC_CODE);
		productKeyMap.put("riceFSC", RICEFSC_CODE);
		productKeyMap.put("salt", SALT_CODE);
		productKeyMap.put("sugar", SUGAR_CODE);
		productKeyMap.put("tamarind", TAMARIND_CODE);
		productKeyMap.put("turmaric", TURMARIC_CODE);
		productKeyMap.put("wheat", WHEAT_CODE);
		productKeyMap.put("rice", RICE_CODE);

		// Auto Weight =====> rice,Wheat,salt,aapriceQty,afscriceQty,fscriceQty
		productMap.put(ATTA_CODE, new Product(ATTA_CODE, SHOW, "atta", "Atta", "attaQty", "Pkt", false,new Integer[]{0,1},false));
		productMap.put(CHILLI_CODE, new Product(CHILLI_CODE, SHOW, "chilli", "Chilli", "chilliQty", "Pkt", false,new Integer[]{0,1},false));
		productMap.put(KEROSENE_CODE, new Product(KEROSENE_CODE, SHOW, "kerosene", "Kerosene", "koilQty", "Ltr", false,new Integer[]{0,1,2,4},false));
		productMap.put(POIL_CODE, new Product(POIL_CODE, SHOW, "poil", "POil", "poilQty", "Ltr", false,new Integer[]{0,1},false));
		productMap.put(RGDAL_CODE, new Product(RGDAL_CODE, SHOW, "rgdal", "RGDal", "rgdalQty", "Pkt", false,new Integer[]{0,1},false));
		productMap.put(RICEAAP_CODE, new Product(RICEAAP_CODE, SHOW, "riceAAP", "AAP Rice", "aapriceQty", "Kg", true,new Integer[]{0,1},false));
		productMap.put(RICEAFSC_CODE, new Product(RICEAFSC_CODE, SHOW, "riceAFSC", "AFSC Rice", "afscriceQty", "Kg", true,new Integer[]{0,1},false));
		productMap.put(RICEFSC_CODE, new Product(RICEFSC_CODE, SHOW, "riceFSC", "FSC Rice", "fscriceQty", "Kg", true,new Integer[]{0,1},false));
		productMap.put(SALT_CODE, new Product(SALT_CODE, SHOW, "salt", "Salt", "saltQty", "Kg", true,new Integer[]{0,1},false));
		productMap.put(SUGAR_CODE, new Product(SUGAR_CODE, SHOW, "sugar", "Sugar", "sugarQty", "Kg", true,new Integer[]{0,1},true));
		productMap.put(TAMARIND_CODE, new Product(TAMARIND_CODE, SHOW, "tamarind", "Tamarind", "tamarindQty", "Pkt", false,new Integer[]{0,1},false));
		productMap.put(TURMARIC_CODE, new Product(TURMARIC_CODE, SHOW, "turmaric", "Turmaric", "turmaricQty", "Pkt", false,new Integer[]{0,1},false));
		productMap.put(WHEAT_CODE, new Product(WHEAT_CODE, SHOW, "wheat", "Wheat", "wheatQty", "Kg", true,new Integer[]{0,1},false));
		productMap.put(RICE_CODE, new Product(RICE_CODE, SHOW, "rice", "Rice", "riceQty", "Kg", true,new Integer[]{0,1},true));

		productPriceMap.put("attaPrice", ATTA_CODE);
		productPriceMap.put("chilliPrice", CHILLI_CODE);
		productPriceMap.put("koilPrice", KEROSENE_CODE);
		productPriceMap.put("poilPrice", POIL_CODE);
		productPriceMap.put("rgdalPrice", RGDAL_CODE);
		productPriceMap.put("aapPrice", RICEAAP_CODE);
		productPriceMap.put("afscPrice", RICEAFSC_CODE);
		productPriceMap.put("fscPrice", RICEFSC_CODE);
		productPriceMap.put("saltPrice", SALT_CODE);
		productPriceMap.put("sugarPrice", SUGAR_CODE);
		productPriceMap.put("tamarindPrice", TAMARIND_CODE);
		productPriceMap.put("turmericPrice", TURMARIC_CODE);
		productPriceMap.put("wheatPrice", WHEAT_CODE);

		productAllotMap.put("attaAllot", ATTA_CODE);
		productAllotMap.put("chillicAllot", CHILLI_CODE);
		productAllotMap.put("koilAllot", KEROSENE_CODE);
		productAllotMap.put("poilAllot", POIL_CODE);
		productAllotMap.put("rgdalAllot", RGDAL_CODE);
		productAllotMap.put("aapAllot", RICEAAP_CODE);
		productAllotMap.put("afscAllot", RICEAFSC_CODE);
		productAllotMap.put("fscAllot", RICEFSC_CODE);
		productAllotMap.put("saltAllot", SALT_CODE);
		productAllotMap.put("sugarAllot", SUGAR_CODE);
		productAllotMap.put("tamarindAllot", TAMARIND_CODE);
		productAllotMap.put("turmericAllot", TURMARIC_CODE);
		productAllotMap.put("wheatAllot", WHEAT_CODE);

		productAllotedMap.put("attaalloted", ATTA_CODE);
		productAllotedMap.put("chillialloted", CHILLI_CODE);
		productAllotedMap.put("kerosenealloted", KEROSENE_CODE);
		productAllotedMap.put("poilalloted", POIL_CODE);
		productAllotedMap.put("rgdalalloted", RGDAL_CODE);
		productAllotedMap.put("aapalloted", RICEAAP_CODE);
		productAllotedMap.put("afscalloted", RICEAFSC_CODE);
		productAllotedMap.put("fscalloted", RICEFSC_CODE);
		productAllotedMap.put("saltalloted", SALT_CODE);
		productAllotedMap.put("sugaralloted", SUGAR_CODE);
		productAllotedMap.put("tamarindalloted", TAMARIND_CODE);
		productAllotedMap.put("turmaricalloted", TURMARIC_CODE);
		productAllotedMap.put("wheatalloted", WHEAT_CODE);
		productAllotedMap.put("ricealloted", RICE_CODE);

		productBalanceMap.put("attabal", ATTA_CODE);
		productBalanceMap.put("chillicbal", CHILLI_CODE);
		productBalanceMap.put("koilbal", KEROSENE_CODE);
		productBalanceMap.put("poilbal", POIL_CODE);
		productBalanceMap.put("rgdalbal", RGDAL_CODE);
		productBalanceMap.put("aapbal", RICEAAP_CODE);
		productBalanceMap.put("afscbal", RICEAFSC_CODE);
		productBalanceMap.put("fscbal", RICEFSC_CODE);
		productBalanceMap.put("saltbal", SALT_CODE);
		productBalanceMap.put("sugarbal", SUGAR_CODE);
		productBalanceMap.put("tamarindbal", TAMARIND_CODE);
		productBalanceMap.put("turmericbal", TURMARIC_CODE);
		productBalanceMap.put("wheatbal", WHEAT_CODE);
		productBalanceMap.put("ricebal", RICE_CODE);

		productLastTransaction.put("attabal", ATTA_CODE);
		productLastTransaction.put("chillibal", CHILLI_CODE);
		productLastTransaction.put("kerosenebal", KEROSENE_CODE);
		productLastTransaction.put("poilbal", POIL_CODE);
		productLastTransaction.put("rgdalbal", RGDAL_CODE);
		productLastTransaction.put("aapbal", RICEAAP_CODE);
		productLastTransaction.put("afscbal", RICEAFSC_CODE);
		productLastTransaction.put("fscbal", RICEFSC_CODE);
		productLastTransaction.put("saltbal", SALT_CODE);
		productLastTransaction.put("sugarbal", SUGAR_CODE);
		productLastTransaction.put("tamarindbal", TAMARIND_CODE);
		productLastTransaction.put("turmaricbal", TURMARIC_CODE);
		productLastTransaction.put("wheatbal", WHEAT_CODE);
		productLastTransaction.put("ricebal", RICE_CODE);

		productIssueReportMap.put("aapriceQty", RICEAAP_CODE);
		productIssueReportMap.put("afscriceQty", RICEAFSC_CODE);
		productIssueReportMap.put("attaQty", ATTA_CODE);
		productIssueReportMap.put("chilliQty", CHILLI_CODE);
		productIssueReportMap.put("fscriceQty", RICEFSC_CODE);
		productIssueReportMap.put("koilQty", KEROSENE_CODE);
		productIssueReportMap.put("poilQty", POIL_CODE);
		productIssueReportMap.put("rgdalQty", RGDAL_CODE);
		productIssueReportMap.put("saltQty", SALT_CODE);
		productIssueReportMap.put("sugarQty", SUGAR_CODE);
		productIssueReportMap.put("tamarindQty", TAMARIND_CODE);
		productIssueReportMap.put("turmericQty", TURMARIC_CODE);
		productIssueReportMap.put("wheatQty", WHEAT_CODE);
		productIssueReportMap.put("kerosene", KEROSENE_CODE);

	}

	public static String getProductCode(String tagName) {
		return productKeyMap.get(tagName);
	}

	public static Product getProductByCode(String productCode) {
		return productMap.get(productCode);
	}

	public static Product getProductByTag(String tagName) {
		return getProductByCode(getProductCode(tagName));
	}

	/**
	 *
	 */
	private ProductMap() {
		// TODO Auto-generated constructor stub
	}

}
