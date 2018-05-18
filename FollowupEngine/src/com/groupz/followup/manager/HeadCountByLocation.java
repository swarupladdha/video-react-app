package com.groupz.followup.manager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.groupz.followup.utils.ConnectionUtils;

public class HeadCountByLocation {
	private JSONObject jsonDivisionObject = new JSONObject();
	private ResultSet selectedRole = null;

	String deleteHeadCount = "delete from headcount_by_location where GroupzId = %s";

	String saveHeadCount = "insert  into headcount_by_location (GroupzId,UserCount,ContactCount,groupzbaseid,"
			+ "location_id,RoleId,Division,subdiv)"
			+ "	  values (%s,%s,%s,'%s',%s,%s,'%s','%s')";

	String saveHeadCountnNew = "insert  into headcount_by_location (groupzbaseid,groupzid,roleid,division,subdiv,month,year,totalcount,usercount,contactcount,enabledcount,disabledcount,location_id)"
			+ "	  values (%d,%d,%d,'%s','%s','%s','%s',%d,%d,%d,%d,%d,%d)";

	// String getHeadCount="select * from headcount where groupzId=%s";

	String getRoleList = "select * from roledefinition where GBROLEID IS NOT NULL and SocietyId=%s";

	String getLocationId = "select LocationId  from apartment where id= %s ";

	String getgroupzBaseId = "select id from builder where id = (select Builderid from apartment where id = %s)";

	String headCountValueQuery = "select count( userflatmapping.id) from userflatmapping,flat,roledefinition,user"
			+ " where userflatmapping.userid=user.id and userflatmapping.flatid=flat.id and userflatmapping.roleid=roledefinition.id"
			+ " and flat.apartmentid=roledefinition.societyid and flat.apartmentid= %s"
			+ " and  user.enabled=true and userflatmapping.enabled=true and flat.contact= %s"
			+ " and roledefinition.id= %s"
			+ " and flat.block_streetdetails= '%s'"
			+ " and flat.subdivision= '%s'";
	String getHeadCountQry = "";

	static final Logger logger = Logger.getLogger(HeadCountByLocation.class);

	public void deleteHeadCountByLocation(Connection connection, int groupzId,
			String month) {
		Statement stmt = null;
		// String builderId = "";
		try {
			String Query = String.format(deleteHeadCount, groupzId);
			stmt = connection.createStatement();
			boolean rs = stmt.execute(Query);
			System.out
					.println("Inside deleteHeadCountByLocation() of HeadCountByLocation Operations inserting from HeadCountByLocation...");
			ConnectionUtils.close(stmt);
		} catch (Exception e) {
			logger.error("Excepton Caught in HeadCountByLocation  Class");
			logger.error(e.getMessage());
			e.printStackTrace();
			ConnectionUtils.close(stmt);
		}
		// TODO Auto-generated method stub
	}

	public void saveHeadCountByLocationNew(Connection connection, int groupzId) {
		Statement stmt = null;
		try {
			String Query = " select f.apartmentid,apt.builderid ,month(f.createddate)month,year(f.createddate) year,ufm.roleid roleid ,f.Block_StreetDetails division,f.subdivision ,sum(ufm.enabled)enabledcount,count(f.id)-sum(ufm.enabled)disabledcount,sum(f.contact)contactcount,count(f.id)-sum(f.contact)usercount,count(f.id)total from flat f ,user u ,userflatmapping ufm ,person  p,apartment apt where ufm.flatid=f.id and f.registeredpersonid=p.id and ufm.userid=u.id and f.apartmentid=apt.id and f.apartmentid in("
					+ groupzId
					+ ") group by month(f.createddate),year(f.createddate),ufm.roleid,f.Block_StreetDetails,f.subdivision";
			System.out.println("Final select Query Is : " + Query);
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(Query);
			while (rs.next()) {
				int location_id = 0;
				int groupzBaseId = rs.getInt("builderid");
				int aptId = rs.getInt("apartmentid");
				int roleId = rs.getInt("roleid");
				int total = rs.getInt("total");
				int usersCount = rs.getInt("usercount");
				int contactCount = rs.getInt("contactcount");
				int enabledCount = rs.getInt("enabledcount");
				int disabledCount = rs.getInt("disabledcount");
				String division = rs.getString("division");
				String subDiv = rs.getString("subDivision");
				String month = rs.getString("month");
				String year = rs.getString("year");
				formInsertAndSave(connection, groupzBaseId, aptId, roleId,
						total, usersCount, contactCount, enabledCount,
						disabledCount, division, subDiv, month, year,
						location_id);
			}
			ConnectionUtils.close(stmt);
		} catch (Exception e) {
			logger.error("Excepton Caught in HeadCountByLocation  Class");
			logger.error(e.getMessage());
			e.printStackTrace();
			ConnectionUtils.close(stmt);
		}
		// TODO Auto-generated method stub
	}

	public void formInsertAndSave(Connection con, int groupzBaseId, int aptId,
			int roleId, int total, int usersCount, int contactCount,
			int enabledCount, int disabledCount, String division,
			String subDiv, String month, String year, int location_id) {
		Statement stmt = null;
		try {
			System.out.println("Inside Inserting New Record For member");
			stmt = con.createStatement();
			String finalInsertQry = String.format(saveHeadCountnNew,
					groupzBaseId, aptId, roleId, division, subDiv, month, year,
					total, usersCount, contactCount, enabledCount,
					disabledCount, location_id);
			System.out.println("Final Insert Query Is : " + finalInsertQry);
			stmt.executeUpdate(finalInsertQry);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ConnectionUtils.close(stmt);
	}

	public void saveHeadCountByLocation(Connection connection, int groupzId,
			String month) {
		Statement stmt = null;
		int groupzBaseId = 0;
		int locationId = 0;
		try {
			String locIdQuery = String.format(getLocationId, groupzId);
			stmt = connection.createStatement();
			ResultSet result = stmt.executeQuery(locIdQuery);
			while (result.next()) {
				locationId = result.getInt("LocationId");
				// System.out.println("locationId"+locationId);
			}
			String getRoleListQuery = String.format(getRoleList, groupzId);
			ResultSet getRoleListSet = stmt.executeQuery(getRoleListQuery);

			String builderQuery = String.format(getgroupzBaseId, groupzId);
			stmt = connection.createStatement();
			ResultSet res = stmt.executeQuery(builderQuery);
			while (res.next()) {
				groupzBaseId = res.getInt("id");
				// System.out.println("groupzBaseId"+groupzBaseId);
			}
			System.out
					.println("Inside saveHeadCountByLocation() of HeadCountByLocation Operations inserting from HeadCountByLocation...");
			while (getRoleListSet.next()) {
				getRoleInfo(getRoleListSet);
				List<String> divList = getdivisionlist();
				// System.out.println(divList);
				if (divList != null && divList.size() > 0) {
					for (String div : divList) {
						List<String> subDivList = getSubivisionList(div);
						for (String subDiv : subDivList) {

							// String GroupzId =
							// getRoleListSet.getString(groupzId);

							String gbroleid = getRoleListSet
									.getString("gbroleid");
							String RoleId = getRoleListSet.getString("Id");
							// String RoleName =
							// getRoleListSet.getString("RoleName");
							String grpId = getRoleListSet
									.getString("SocietyId");
							int grpId1 = Integer.parseInt(grpId);

							JSONObject roleInfoJSON = JSONObject
									.fromObject(getRoleListSet
											.getString("roleInformation"));
							int maxCount = getTargetCount(roleInfoJSON, div,
									subDiv);
							int userCount = getHeadCountValue(grpId1, RoleId,
									div, subDiv, false, connection);
							int contactCount = getHeadCountValue(grpId1,
									RoleId, div, subDiv, true, connection);
							// System.out.println("----->"+userCount+""+contactCount);
							stmt = connection.createStatement();
							System.out.println("Before Save Head Count");
							String getsaveHeadCountQuery = String.format(
									saveHeadCount, groupzId, userCount,
									contactCount, groupzBaseId, locationId,
									gbroleid, div, subDiv);
							System.out.println("getsaveHeadCountQuery : "
									+ getsaveHeadCountQuery);
							boolean saveHeadCountSet = stmt
									.execute(getsaveHeadCountQuery);
							// System.out.println(saveHeadCountSet);
						}
					}
				}
			}
			ConnectionUtils.close(stmt);
		} catch (Exception e) {

			logger.error("Excepton Caught in HeadCountByLocation  Class");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private int getHeadCountValue(int groupzId, String roleId, String div,
			String subDiv, boolean b, Connection connection) {
		Statement stmt = null;
		int getHeadCountValue = 0;
		try {
			stmt = connection.createStatement();
			String getRoleListQuery = String.format(headCountValueQuery,
					groupzId, b, roleId, div, subDiv);
			ResultSet headCountValueSet = stmt.executeQuery(getRoleListQuery);
			if (headCountValueSet.first()) {
				getHeadCountValue = headCountValueSet.getInt(1);
			}
		} catch (Exception e) {
			logger.error("Exception Caught in HeadCountByLocation  getHeadCountValue()");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		ConnectionUtils.close(stmt);
		return getHeadCountValue;
	}

	private int getTargetCount(JSONObject roleInfoJSON, String div,
			String subDiv) {
		int count = 0;
		try {
			if (roleInfoJSON != null && roleInfoJSON.isEmpty() == false) {

				if (div != null && div.isEmpty() == false) {
					if (roleInfoJSON.containsKey(div.trim()) == true) {
						JSONArray jsonSubdivJSONArray = roleInfoJSON
								.getJSONArray(div.trim());
						// System.out.println("1ST TARGET COUNT");

						if (jsonSubdivJSONArray != null
								&& jsonSubdivJSONArray.isEmpty() == false) {

							for (int p = 0; p < jsonSubdivJSONArray.size(); p++) {
								// System.out.println("2ND TARGET COUNT");
								JSONObject jbj = jsonSubdivJSONArray
										.getJSONObject(p);
								// System.out.println("3RD TARGET COUNT");
								if (jbj.containsKey(subDiv) == true) {
									count = Integer.parseInt(""
											+ jbj.get(subDiv.trim()));
									// System.out.println("4TH TARGET COUNT:"+count);
									break;
								}
								// System.out.println("5TH TARGET COUNT");

							}
						} else {
							// sub div name is not present so count 0
						}
					} else {
						// div name not present so count 0
					}
				}

			} else {
				// json empty
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	public List<String> getSubivisionList(String divName) {
		List<String> subDivList = new ArrayList<String>();
		if (divName == null || divName.isEmpty() == true) {
			subDivList = new ArrayList<String>();
		} else {
			try {
				JSONArray subDivArray = jsonDivisionObject
						.optJSONArray(divName);
				// System.out.println("------------>"+subDivArray);
				if (subDivArray != null && subDivArray.isEmpty() == false
						&& subDivArray.size() > 0) {
					for (int s = 0; s < subDivArray.size(); s++) {
						JSONObject sdObj = subDivArray.getJSONObject(s);
						Set sdKeys = sdObj.keySet();
						for (Object key : sdKeys) {
							String sdName = (String) key;
							subDivList.add(sdName);
						}
					}
				} else {
					// System.out.println(" SUBDIV ARRAY IS NULL ");
					subDivList = new ArrayList<String>();
				}

			} catch (Exception e) {
				// System.out.println(" ERROR IN SUBDIVLIST ROLEINFOMANAGER ");
			}

		}
		if (subDivList == null || subDivList.size() == 0) {
			/*
			 * if (selectedRole != null) { if (selectedRole.getSociety() !=
			 * null) { subDivList = (List<String>) selectedRole.getSociety()
			 * .getAvailableSubDivisions(); } }
			 */
			subDivList = new ArrayList<String>();
		}

		return subDivList;
	}

	private List<String> getdivisionlist() {

		Set itKeys = jsonDivisionObject.keySet();
		List<String> divList = new ArrayList<String>();
		for (Object key : itKeys) {
			String divName = (String) key;
			// System.out.println(" Divisionname to add list :--> " + divName);
			divList.add(divName);
		}
		if (divList == null || divList.size() == 0) {
			/*
			 * if (selectedRole != null) { if (selectedRole.getSociety() !=
			 * null) { divList = (List<String>) selectedRole.getSociety()
			 * .getAvailableBlockStreets(); } }
			 */
			divList = new ArrayList<String>();
		}
		return divList;
		// TODO Auto-generated method stub

	}

	private void getRoleInfo(ResultSet r) throws SQLException {
		selectedRole = r;
		String jsonString = r.getString("roleInformation");
		// System.out.println("UPDATED JSON STRING:"+jsonString);
		if (jsonString == null || jsonString.equalsIgnoreCase("null") == true) {

			jsonDivisionObject = new JSONObject();

		} else {

			if (jsonString != null && jsonString.isEmpty() == false) {

				jsonDivisionObject = JSONObject.fromObject(jsonString);

			} else {

				jsonDivisionObject = new JSONObject();
			}

		}

	}

}