package interfaceApplication;

import java.util.HashMap;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import apps.appsProxy;
import authority.privilige;
import database.db;
import esayhelper.JSONHelper;
import esayhelper.TimeHelper;
import esayhelper.jGrapeFW_Message;
import model.MenuModel;
import nlogger.nlogger;
import rpc.execRequest;
import session.session;

public class Menu {
	private static MenuModel model;
	private static session session;
	private static JSONObject _obj;
	private static HashMap<String, Object> map;
	private static JSONObject UserInfo = new JSONObject();

	static {
		model = new MenuModel();
		session = new session();
		_obj = new JSONObject();
		map = new HashMap<String, Object>();
	}

	public Menu() {
		UserInfo = session.getSession((String) execRequest.getChannelValue("sid"));
	}

	/**
	 * 新增菜单
	 * 
	 * @project GrapeMenu
	 * @package interfaceApplication
	 * @file Menu.java
	 * 
	 * @param mString
	 *            待操作数据
	 * @return {"message":"新增菜单成功","errorcode":0} 或
	 *         {"message":"其他异常","errorcode":99}
	 *
	 */
	public String AddMenu(String mString) {
		int role = getRoleSign();
		int code = 99;
		if (role == 5) {
			try {
				JSONObject object = model.check(mString, def());
				if (object == null) {
					return resultMessage(1);
				}
				code = model.getdb().data(object).insertOnce() != null ? 0 : 99;
			} catch (Exception e) {
				nlogger.logout(e);
				code = 99;
			}
		}else{
			code = 2;
		}
		return resultMessage(code, "新增菜单成功");
	}

	/**
	 * 菜单修改
	 * 
	 * @project GrapeMenu
	 * @package interfaceApplication
	 * @file Menu.java
	 * 
	 * @param id
	 *            菜单id
	 * @param mString
	 *            待操作数据
	 * @return {"message":"修改菜单成功","errorcode":0} 或
	 *         {"message":"其他异常","errorcode":99}
	 *
	 */
	public String UpdateMenu(String id, String mString) {
		int code = 99;
		try {
			code = model.getdb().eq("_id", new ObjectId(id)).data(mString).update() != null ? 0 : 99;
		} catch (Exception e) {
			nlogger.logout(e);
			code = 99;
		}
		return resultMessage(code, "修改菜单成功");
	}

	/**
	 * 删除菜单
	 * 
	 * @project GrapeMenu
	 * @package interfaceApplication
	 * @file Menu.java
	 * 
	 * @param id
	 *            菜单id
	 * @return {"message":"删除菜单成功","errorcode":0} 或
	 *         {"message":"其他异常","errorcode":99}
	 *
	 */
	public String DeleteMenu(String id) {
		int code = 99;
		try {
			code = model.getdb().eq("_id", new ObjectId(id)).delete() != null ? 0 : 99;
		} catch (Exception e) {
			nlogger.logout(e);
			code = 99;
		}
		return resultMessage(code, "删除菜单成功");
	}

	/**
	 * 批量删除菜单
	 * 
	 * @project GrapeMenu
	 * @package interfaceApplication
	 * @file Menu.java
	 * 
	 * @param id
	 *            菜单id ，id之间使用","分隔
	 * @return {"message":"批量删除菜单成功","errorcode":0} 或
	 *         {"message":"其他异常","errorcode":99}
	 *
	 */
	public String DeleteBatchMenu(String id) {
		db db = model.getdb().or();
		int code = 99;
		try {
			String[] value = id.split(",");
			for (String ids : value) {
				db.eq("_id", new ObjectId(ids));
			}
			code = db.deleteAll() == value.length ? 0 : 99;
		} catch (Exception e) {
			nlogger.logout(e);
			code = 99;
		}
		return resultMessage(code, "删除菜单成功");
	}

	/**
	 * 根据当前登录的用户，显示属于该用户的菜单
	 * 
	 * @project GrapeMenu
	 * @package interfaceApplication
	 * @file Menu.java
	 * 
	 * @return {"message":"records":[{"":""},{"":""}],"errords":0} 或
	 *         {"message":"records":[],"errords":0}
	 *
	 */
	public String ShowMenu() {
		JSONArray array = null;
		try {
			String prvid = (String) UserInfo.get("ugid");
			array = new JSONArray();
			array = model.getdb().eq("state", 0).eq("prvid", prvid).select();
		} catch (Exception e) {
			nlogger.logout(e);
			array = null;
		}
		return resultMessage(array);
	}

	/**
	 * 设置菜单状态
	 * 
	 * @project GrapeMenu
	 * @package interfaceApplication
	 * @file Menu.java
	 * 
	 * @param id
	 *            菜单id
	 * @param state
	 *            状态值
	 * @return {"message":"菜单状态设置成功","errorcode":0} 或
	 *         {"message":"其他异常","errorcode":99}
	 */
	public String SetState(String id, String state) {
		int code = 99;
		String string = "{\"state\":" + state + "}";
		if (JSONHelper.string2json(state) != null) {
			string = state;
		}
		try {
			code = model.getdb().eq("_id", new ObjectId(id)).data(string).update() != null ? 0 : 99;
		} catch (Exception e) {
			nlogger.logout(e);
			code = 99;
		}
		return resultMessage(code, "菜单状态设置成功");
	}

	/**
	 * 根据角色plv，获取角色级别
	 * 
	 * @project GrapeSuggest
	 * @package interfaceApplication
	 * @file Suggest.java
	 * 
	 * @return
	 *
	 */
	private int getRoleSign() {
		int roleSign = 0; // 游客
		String sid = (String) execRequest.getChannelValue("sid");
		if (!sid.equals("0")) {
			try {
				privilige privil = new privilige(sid);
				int roleplv = privil.getRolePV();
				if (roleplv >= 1000 && roleplv < 3000) {
					roleSign = 1; // 普通用户即企业员工
				}
				if (roleplv >= 3000 && roleplv < 5000) {
					roleSign = 2; // 栏目管理员
				}
				if (roleplv >= 5000 && roleplv < 8000) {
					roleSign = 3; // 企业管理员
				}
				if (roleplv >= 8000 && roleplv < 10000) {
					roleSign = 4; // 监督管理员
				}
				if (roleplv >= 10000) {
					roleSign = 5; // 总管理员
				}
			} catch (Exception e) {
				nlogger.logout(e);
				roleSign = 0;
			}
		}
		return roleSign;
	}

	private HashMap<String, Object> def() {
		map.put("ownid", appsProxy.appid());
		map.put("prvid", (String) UserInfo.get("ugid"));
		map.put("parent", "0");
		map.put("sort", 0);
		map.put("state", 0);
		map.put("data", new JSONObject());
		map.put("time", String.valueOf(TimeHelper.nowMillis()));
		return map;
	}

	@SuppressWarnings("unchecked")
	private String resultMessage(JSONArray array) {
		if (array == null) {
			array = new JSONArray();
		}
		_obj.put("records", array);
		return resultMessage(0, _obj.toString());
	}

	private String resultMessage(int num) {
		return resultMessage(num, "");
	}

	private String resultMessage(int num, String message) {
		String msg = "";
		switch (num) {
		case 0:
			msg = message;
			break;
		case 1:
			msg = "必填字段没有填";
			break;
		case 2:
			msg = "只有系统管理员用户才可以新增菜单";
			break;
		default:
			msg = "其他操作异常";
			break;
		}
		return jGrapeFW_Message.netMSG(num, msg);
	}
}
