package manager;

import java.sql.Connection;

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
				System.out.println("Calling get list of countrys");
				return getListOfCountrys(serviceType, functionType, dataObj,
						connection);
			} /*else if (functionType == Integer.parseInt(PropertiesUtil
					.getProperty("GetStates"))) {
				return getListOfStates();
			} else if (functionType == Integer.parseInt(PropertiesUtil
					.getProperty("GetCities"))) {
				return getListOfCities();
			}*/ else {
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

	public String getListOfCountrys(int serviceType, int functionType,
			JSONObject dataObj, Connection con) {
		String paginationSQL = "";

		int limit = 0;
		int offset = 0;
		if (dataObj.containsKey(GlobalVariables.LIMIT_TAG)) {
			limit = dataObj.getInt(GlobalVariables.LIMIT_TAG);

		}
		if (dataObj.containsKey(GlobalVariables.OFFSET_TAG)) {
			offset = dataObj.getInt(GlobalVariables.OFFSET_TAG);
		}
		paginationSQL = utils.paginationQry(limit, offset);
		String SQL = "Select * from Country " + paginationSQL;
		return dbo.getCountryDetails(SQL, con).toJSONString();

	}

	/*public String getListOfStates() {

	}

	public String getListOfCities() {

	}*/

}
