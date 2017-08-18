package manager;

import java.sql.Connection;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import operations.DBOperations;
import utils.ConnectionPooling;
import utils.PropertiesUtil;
import utils.RestUtils;

public class GeographyManager {
	RestUtils utils = new RestUtils();
	DBOperations dbo = new DBOperations();

	public String getGeographyResponse(String req) {
		ConnectionPooling connectionPooling = ConnectionPooling.getInstance();
		Connection connection = null;
		try {
			if (utils.isJSONValid(req) == false) {
				return utils.processError(
						PropertiesUtil.getProperty("invalid_json_code"),
						PropertiesUtil.getProperty("invalid_json_message"));

			}

			connection = connectionPooling.getConnection();

			JSONObject obj = JSONObject.fromObject(req);
			JSONObject dataObj = obj.getJSONObject("data");

			int serviceType = obj.getInt(GlobalVariables.SERVICE_TYPE_TAG);
			int functionType = obj.getInt(GlobalVariables.FUNCTION_TYPE_TAG);

			if (serviceType != Integer.parseInt(PropertiesUtil
					.getProperty("geography_module"))) {
				return utils.processError(PropertiesUtil
						.getProperty("invalid_servicetype_code"),
						PropertiesUtil
								.getProperty("invalid_servicetype_message"));
			}
			if (functionType == Integer.parseInt(PropertiesUtil
					.getProperty("GetCountries"))) {
				System.out.println("Calling get list of countries");
				return getListOfCountrys(serviceType, functionType, dataObj,
						connection);
			} else if (functionType == Integer.parseInt(PropertiesUtil
					.getProperty("GetStates"))) {
				System.out.println("Calling get list of states");
				return getListOfStates(serviceType, functionType, dataObj,
						connection);
			} else if (functionType == Integer.parseInt(PropertiesUtil
					.getProperty("GetCities"))) {
				System.out.println("Calling get list of cities");
				return getListOfCities(serviceType, functionType, dataObj,
						connection);
			} else if (functionType == Integer.parseInt(PropertiesUtil
					.getProperty("GetAreas"))) {
				System.out.println("Calling get list of Areas");
				return getListOfAreas(serviceType, functionType, dataObj,
						connection);
			} else if (functionType == Integer.parseInt(PropertiesUtil
					.getProperty("getDetailsBasedOnZIPCode"))) {
				System.out.println("Calling get details based on ZIP");
				return getDetailsBasedOnZIPCode(serviceType, functionType,
						dataObj, connection);
			} else {
				return utils
						.processError(
								PropertiesUtil
										.getProperty("invalid_functiontype_code"),
								PropertiesUtil
										.getProperty("invalid_functiontype_message"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return utils.processError(
					PropertiesUtil.getProperty("general_error_code"),
					PropertiesUtil.getProperty("general_error_message"));
		} finally {
			if (connection != null) {
				connectionPooling.close(connection);
			}
		}
	}

	/**** GET LIST OF ALL COUNTRIES ****/
	public String getListOfCountrys(int serviceType, int functionType,
			JSONObject dataObj, Connection con) {
		String paginationSQL = "";
		int limit = -1;
		int offset = -1;
		try {
			if (dataObj.containsKey(GlobalVariables.LIMIT_TAG)) {
				limit = dataObj.getInt(GlobalVariables.LIMIT_TAG);

			}
			if (dataObj.containsKey(GlobalVariables.OFFSET_TAG)) {
				offset = dataObj.getInt(GlobalVariables.OFFSET_TAG);
			}
			paginationSQL = utils.paginationQry(limit, offset);
			String SQL = "Select * from Country " + paginationSQL;
			System.out.println(SQL);
			JSONArray dataArray = dbo.getGeographyDetails(SQL, con,
					GlobalVariables.COUNTRY_LIST);
			if (dataArray != null && dataArray.size() > 0) {
				return utils.processSuccessWithJsonArray(serviceType,
						functionType, dataArray);
			} else {
				return utils.processError(
						PropertiesUtil.getProperty("countries_empty_code"),
						PropertiesUtil.getProperty("countries_empty_message"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return utils.processError(
					PropertiesUtil.getProperty("general_error_code"),
					PropertiesUtil.getProperty("general_error_message"));

		}

	}

	/**** LISTING STATES ****/
	public String getListOfStates(int serviceType, int functionType,
			JSONObject dataObj, Connection con) {
		String paginationSQL = "";
		int limit = -1;
		int offset = -1;
		int countryId = -1;
		try {
			countryId = dataObj.getInt(GlobalVariables.COUNTRYID_TAG);

			if (dataObj.containsKey(GlobalVariables.LIMIT_TAG)) {
				limit = dataObj.getInt(GlobalVariables.LIMIT_TAG);

			}
			if (dataObj.containsKey(GlobalVariables.OFFSET_TAG)) {
				offset = dataObj.getInt(GlobalVariables.OFFSET_TAG);
			}
			paginationSQL = utils.paginationQry(limit, offset);
			String getStatesSQL = "Select * from State where CountryId="
					+ countryId + paginationSQL;
			System.out.println("Get states Query :" + getStatesSQL);
			JSONArray dataArray = dbo.getGeographyDetails(getStatesSQL, con,
					GlobalVariables.STATE_LIST);
			if (dataArray != null && dataArray.size() > 0) {
				return utils.processSuccessWithJsonArray(serviceType,
						functionType, dataArray);
			} else {
				return utils.processError(
						PropertiesUtil.getProperty("states_empty_code"),
						PropertiesUtil.getProperty("states_empty_message"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return utils.processError(
					PropertiesUtil.getProperty("general_error_code"),
					PropertiesUtil.getProperty("general_error_message"));
		}
	}

	public String getListOfCities(int serviceType, int functionType,
			JSONObject dataObj, Connection con) {
		int limit = -1;
		int offset = -1;
		int stateId = -1;
		try {
			stateId = dataObj.getInt(GlobalVariables.STATEID_TAG);

			if (dataObj.containsKey(GlobalVariables.LIMIT_TAG)) {
				limit = dataObj.getInt(GlobalVariables.LIMIT_TAG);

			}
			if (dataObj.containsKey(GlobalVariables.OFFSET_TAG)) {
				offset = dataObj.getInt(GlobalVariables.OFFSET_TAG);
			}
			String paginationSQL = utils.paginationQry(limit, offset);
			String getCitiesSQL = "Select * from City where StateId=" + stateId
					+ paginationSQL;
			System.out.println("Get Cities SQL :" + getCitiesSQL);
			JSONArray dataArray = dbo.getGeographyDetails(getCitiesSQL, con,
					GlobalVariables.CITY_LIST);
			if (dataArray != null && dataArray.size() > 0) {
				return utils.processSuccessWithJsonArray(serviceType,
						functionType, dataArray);
			} else {
				return utils.processError(
						PropertiesUtil.getProperty("city_empty_code"),
						PropertiesUtil.getProperty("city_empty_message"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return utils.processError(
					PropertiesUtil.getProperty("general_error_code"),
					PropertiesUtil.getProperty("general_error_message"));
		}

	}

	public String getListOfAreas(int serviceType, int functionType,
			JSONObject dataObj, Connection con) {
		int limit = -1;
		int offset = -1;
		int cityId = -1;
		try {
			cityId = dataObj.getInt("cityid");
			if (dataObj.containsKey(GlobalVariables.LIMIT_TAG)) {
				limit = dataObj.getInt(GlobalVariables.LIMIT_TAG);
			}
			if (dataObj.containsKey(GlobalVariables.OFFSET_TAG)) {
				offset = dataObj.getInt(GlobalVariables.OFFSET_TAG);
			}
			String paginationSQL = utils.paginationQry(limit, offset);

			String getAreasSQL = "Select * from Area where CityId=" + cityId
					+ paginationSQL;
			System.out.println("Get Areas SQL :" + getAreasSQL);
			JSONArray dataArray = dbo.getGeographyDetails(getAreasSQL, con,
					GlobalVariables.AREA_LIST);
			if (dataArray != null && dataArray.size() > 0) {
				return utils.processSuccessWithJsonArray(serviceType,
						functionType, dataArray);
			} else {
				return utils.processError(
						PropertiesUtil.getProperty("area_empty_code"),
						PropertiesUtil.getProperty("area_empty_message"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return utils.processError(
					PropertiesUtil.getProperty("general_error_code"),
					PropertiesUtil.getProperty("general_error_message"));
		}

	}

	public String getDetailsBasedOnZIPCode(int serviceType, int functionType,
			JSONObject dataObj, Connection con) {
		try {
			String zipCode = dataObj.getString("zipcode");
			JSONObject zipDetails = dbo.getDetailsBasedOnZip(zipCode, con);
			if (zipDetails != null) {
				return utils.processSuccessWithJsonObject(serviceType,
						functionType, zipDetails);
			} else {
				return utils.processError(
						PropertiesUtil.getProperty("no_matches_code"),
						PropertiesUtil.getProperty("no_matches_message"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return utils.processError(
					PropertiesUtil.getProperty("general_error_code"),
					PropertiesUtil.getProperty("general_error_message"));
		}

	}

}
