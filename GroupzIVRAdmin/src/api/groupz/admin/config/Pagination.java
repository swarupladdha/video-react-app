package api.groupz.admin.config;

public class Pagination {
	
	public  String paginationQry(int limit, int offset) {
		String paginationQry = "";
		
		if (limit != -1 && offset != -1) {
			paginationQry = " limit " + limit + " offset " + offset;

		}
		if (limit != -1 && offset == -1) {
			paginationQry = " limit " + limit;

		}
		if (limit == -1 && offset != -1) {
			paginationQry = " offset " + offset;

		}
		return paginationQry;
	}

}
