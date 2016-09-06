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

import com.groupz.followup.operations.HeadCountOperations;
import com.groupz.followup.utils.ConnectionUtils;

public class HeadCountByLocation {
	private JSONObject jsonDivisionObject = new JSONObject();
	private ResultSet selectedRole = null;
	
	String deleteHeadCount="delete from headcount_by_location where GroupzId = %s"; 
	
	String saveHeadCount="insert  into headcount_by_location (GroupzId,UserCount,ContactCount,groupzbaseid,"
			+ "location_id,RoleId,Division,subdiv)"
			+ "	  values (%s,%s,%s,'%s',%s,%s,'%s','%s')" ;
	
	String getHeadCount="select * from headcount where groupzId=%s";
	
	String getRoleList = "select * from roledefinition where GBROLEID IS NOT NULL and SocietyId=%s";
	
	String getLocationId="select LocationId  from apartment where id= %s ";
	
	String getgroupzBaseId="select id from builder where id = (select Builderid from apartment where id = %s)";

	String headCountValueQuery = "select count( userflatmapping.id) from userflatmapping,flat,roledefinition,user"
			+ " where userflatmapping.userid=user.id and userflatmapping.flatid=flat.id and userflatmapping.roleid=roledefinition.id"
			+ " and flat.apartmentid=roledefinition.societyid and flat.apartmentid= %s"
			+ " and  user.enabled=true and userflatmapping.enabled=true and flat.contact= %s"
			+ " and roledefinition.id= %s"
			+ " and flat.block_streetdetails= '%s'"
			+ " and flat.subdivision= '%s'";
	
	
	static final Logger logger = Logger.getLogger(HeadCountByLocation.class);
	
	public void deleteHeadCountByLocation(Connection connection, int groupzId,
			String month) {
		Statement stmt = null;
		String builderId ="";
		
		
		try {
			String Query = String.format(deleteHeadCount,groupzId);
			stmt = connection.createStatement();
			boolean rs = stmt.execute(Query);
			System.out.println("deleted");
		
	} catch (Exception e) {
		ConnectionUtils.close(stmt);
		logger.error("Excepton Caught in HeadCountByLocation  Class");
		logger.error(e.getMessage());
		e.printStackTrace();
	}
		// TODO Auto-generated method stub
		
		
	}

	public void saveHeadCountByLocation(Connection connection, int groupzId,
			String month) {
		Statement stmt = null;
		HeadCountOperations hco = new HeadCountOperations();
		String builderId ="";
		int groupzBaseId = 0 ;
		String country ="";
		String state ="";
		String city ="";
		String latitude ="";
		String longitude ="";
		int locationId=0;
		
		try {

			String locIdQuery = String.format(getLocationId,groupzId);
			stmt = connection.createStatement();
			ResultSet result = stmt.executeQuery(locIdQuery);
			while (result.next()) {
				locationId = result.getInt("LocationId");
				System.out.println("locationId"+locationId);
			}
			String getHeadCountList = String.format(getHeadCount,groupzId);
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(getHeadCountList);
			stmt = connection.createStatement();
			String getRoleListQuery = String.format(getRoleList, groupzId);
			ResultSet getRoleListSet = stmt.executeQuery(getRoleListQuery);
			
			String builderQuery = String.format(getgroupzBaseId,groupzId);
			stmt = connection.createStatement();
			ResultSet res = stmt.executeQuery(builderQuery);
			while (res.next()) {
				groupzBaseId = res.getInt("id");
				System.out.println("groupzBaseId"+groupzBaseId);
			}
			
			
			
			System.out.println("Inside saveHeadcount() of HeadCountOperations inserting from head count...");
			while (getRoleListSet.next()) {
				getRoleInfo(getRoleListSet);

				List<String> divList = getdivisionlist();
				// System.out.println(divList);
				if (divList != null && divList.size() > 0) {
					for (String div : divList) {
						List<String> subDivList = getSubivisionList(div);
						for (String subDiv : subDivList) {

						//	String GroupzId = getRoleListSet.getString(groupzId);

							String gbroleid = getRoleListSet.getString("gbroleid");
							String RoleId = getRoleListSet.getString("Id");
							String RoleName = getRoleListSet.getString("RoleName");
							String grpId=getRoleListSet.getString("SocietyId");
							int grpId1=Integer.parseInt(grpId);

							JSONObject roleInfoJSON = JSONObject.fromObject(getRoleListSet.getString("roleInformation"));
							int maxCount = getTargetCount(roleInfoJSON, div,subDiv);
							int userCount = getHeadCountValue(grpId1, RoleId, div, subDiv, false, connection);
							int contactCount = getHeadCountValue(grpId1, RoleId, div, subDiv, true, connection);
							// System.out.println("----->"+userCount+""+contactCount);
							if (userCount != -1) {
								userCount = userCount;
								// System.out.println("usercount"+userCount);
							}
							if (contactCount != -1) {
								contactCount = contactCount;
								// System.out.println("contactCount"+contactCount);
							}

							stmt = connection.createStatement();
							String getsaveHeadCountQuery = String.format(saveHeadCount,groupzId,userCount, contactCount, groupzBaseId,
									locationId, gbroleid, div,subDiv);
		
							System.out.println(getsaveHeadCountQuery);
							boolean saveHeadCountSet = stmt.execute(getsaveHeadCountQuery);
												
							// System.out.println(saveHeadCountSet);
						}
					}
				}
			
			
			System.out.println("buil");
			}
	} catch (Exception e) {
		ConnectionUtils.close(stmt);
		logger.error("Excepton Caught in HeadCountByLocation  Class");
		logger.error(e.getMessage());
		e.printStackTrace();
	}
		// TODO Auto-generated method stub
		
	}

	
	

	private int getHeadCountValue(int groupzId, String roleId, String div,
			String subDiv, boolean b, Connection connection)
			throws SQLException {
		Statement stmt = connection.createStatement();
		int getHeadCountValue = 0;
		String getRoleListQuery = String.format(headCountValueQuery, groupzId, b, roleId, div, subDiv);
		ResultSet headCountValueSet = stmt.executeQuery(getRoleListQuery);
		if (headCountValueSet.first())			
		{
			getHeadCountValue = headCountValueSet.getInt(1);
			// System.out.println("=====>"+headCountValueSet.getInt(1));
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