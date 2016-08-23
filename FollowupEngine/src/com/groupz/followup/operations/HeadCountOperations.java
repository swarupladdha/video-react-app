package com.groupz.followup.operations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.groupz.followup.utils.ConnectionUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class HeadCountOperations {
	private JSONObject jsonDivisionObject = new JSONObject();
	private ResultSet selectedRole = null;
	
	 String getApartmentById="select * from apartment where id = %s";

	 String deleteHeadCountQuery = "delete from headcount where GroupzId = %s ";

	 String getRoleList = "select * from roledefinition where GBROLEID IS NOT NULL and SocietyId=%s";

	 String saveHeadCountQuery = "insert into headcount (ContactCount,Division,GroupzId,MaxCount,RoleId,"
			+ "RoleName,subDivision,UserCount)values(%s,'%s',%s,%s,%s,'%s','%s',%s)";

	 String headCountValueQuery = "select count( userflatmapping.id) from userflatmapping,flat,roledefinition,user"
			+ " where userflatmapping.userid=user.id and userflatmapping.flatid=flat.id and userflatmapping.roleid=roledefinition.id"
			+ " and flat.apartmentid=roledefinition.societyid and flat.apartmentid= %s"
			+ " and  user.enabled=true and userflatmapping.enabled=true and flat.contact= %s"
			+ " and roledefinition.id= %s"
			+ " and flat.block_streetdetails= '%s'"
			+ " and flat.subdivision= '%s'";

	public void deleteHeadCount(Connection connection, int groupzId) {
		Statement stmt=null;
		try {
			stmt= connection.createStatement();
			System.out.println(" Inside deleteHeadCount() of HeadCountOperations deleting from head count");
			String deleteFeeAggQuery = String.format(deleteHeadCountQuery,
					groupzId);
			System.out.println("Inside deleteHeadCount() of HeadCountOperations deleted from head count");
			//System.out.println(deleteFeeAggQuery);
			boolean deletedFeeAggSet = stmt.execute(deleteFeeAggQuery);
			if (deletedFeeAggSet) {
				// System.out.println("deleted");
			}
			ConnectionUtils.close(stmt);
		} catch (SQLException e) {
			ConnectionUtils.close(stmt);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void saveHeadcount(Connection connection, int groupzId) {
		Statement stmt = null;
		
		try {
			stmt = connection.createStatement();
			String getRoleListQuery = String.format(getRoleList, groupzId);
			ResultSet getRoleListSet = stmt.executeQuery(getRoleListQuery);
			System.out.println(getRoleListSet);
			stmt = connection.createStatement();
			String getapt = String.format(getApartmentById, groupzId);
			ResultSet apt = stmt.executeQuery(getapt);
			
			
			if (apt != null) {
				
				while(getRoleListSet.next()) {
					getRoleInfo(getRoleListSet);
					 
					List<String> divList = getdivisionlist();
					if (divList != null && divList.size() > 0) {
						for (String div : divList) {
							List<String> subDivList = getSubivisionList(div);
							for (String subDiv : subDivList) {
								String RoleId = getRoleListSet.getString("ID");
								String RoleName = getRoleListSet.getString("RoleName");
								String grpId=getRoleListSet.getString("SocietyId");
								int grpId1=Integer.parseInt(grpId);
								
								JSONObject roleInfoJSON = JSONObject.fromObject(getRoleListSet.getString("roleInformation"));
								System.out.println("roleInfoJSON"+roleInfoJSON);
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
								String getsaveHeadCountQuery = String.format(saveHeadCountQuery, contactCount, div, grpId,
										maxCount, RoleId, RoleName,subDiv, userCount);
			
								System.out.println(getsaveHeadCountQuery);
								boolean saveHeadCountSet = stmt.execute(getsaveHeadCountQuery);
													
								// System.out.println(saveHeadCountSet);
							}
						}
					}

				}
			}
		} catch (SQLException e) {
			ConnectionUtils.close(stmt);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private int getHeadCountValue(int groupzId, String roleId, String div,
			String subDiv, boolean b, Connection connection)
			throws SQLException {
		Statement stmt = connection.createStatement();
		int getHeadCountValue = 0;
		String getRoleListQuery = String.format(headCountValueQuery, groupzId, b, roleId, div, subDiv);
		ResultSet headCountValueSet = stmt.executeQuery(getRoleListQuery);
		System.out.println(getRoleListQuery);
		if (headCountValueSet.first())			
		{
			getHeadCountValue = headCountValueSet.getInt(1);
			System.out.println("=====>"+headCountValueSet.getInt(1));
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

}
