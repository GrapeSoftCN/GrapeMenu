package interfaceApplication;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import JGrapeSystem.jGrapeFW_Message;
import apps.appsProxy;
import authority.plvDef.UserMode;
import database.db;
import interfaceModel.GrapeDBSpecField;
import interfaceModel.GrapeTreeDBModel;
import json.JSONHelper;
import nlogger.nlogger;
import session.session;
import string.StringHelper;

public class Menu {
	private GrapeTreeDBModel menus;
	private GrapeDBSpecField gdbField;
	private session se;
	private JSONObject _obj;
	private JSONObject UserInfo = new JSONObject();

	public Menu() {
		menus = new GrapeTreeDBModel();
		gdbField = new GrapeDBSpecField();
        gdbField.importDescription(appsProxy.tableConfig("menu"));
        menus.descriptionModel(gdbField);
        menus.bindApp();
		
		se = new session();
		_obj = new JSONObject();
		String sid = session.getSID();
		nlogger.logout(sid);
		if (sid != null) {
			UserInfo = se.getDatas();
			nlogger.logout("uid: "+se.getUID());
			nlogger.logout("data: "+se.getDatas());
		}
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
		int code = 99;
		try {
			JSONObject object = JSONObject.toJSON(mString);
			if (object == null) {
				return resultMessage(1);
			}
			code = menus.data(object).insertOnce() != null ? 0 : 99;
		} catch (Exception e) {
			nlogger.logout(e);
			code = 99;
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
			code = menus.eq("_id", new ObjectId(id)).data(mString).update() != null ? 0 : 99;
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
			code = menus.eq("_id", new ObjectId(id)).delete() != null ? 0 : 99;
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
		db db = menus.or();
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
			array = new JSONArray();
				String prvid = (String) UserInfo.get("ugid");
				array =menus.eq("state", 0).like("prvid", prvid).select();
		} catch (Exception e) {
			nlogger.logout(e);
			array = null;
		}
		return resultMessage(array);
	}
	/**
	 * 判断当前用户是否为管理员
	 * 
	 * @return
	 */
	public boolean isAdmin() {
		int userType = 0;
		session se = new session();
		JSONObject userInfo = se.getDatas();
		userType = (userInfo != null && userInfo.size() != 0) ? Integer.parseInt(userInfo.getString("userType")) : 0;
		return UserMode.admin == userType;
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
		if (!state.contains("state")) {
			state = "{\"state\":" + state + "}";
		}
		// String string = "{\"state\":" + state + "}";
		// if (JSONHelper.string2json(state) != null) {
		// string = state;
		// }
		try {
			code = menus.eq("_id", new ObjectId(id)).data(state).update() != null ? 0 : 99;
		} catch (Exception e) {
			nlogger.logout(e);
			code = 99;
		}
		return resultMessage(code, "菜单状态设置成功");
	}

	public String SetRoles(String rid, String Info) {
		String result = resultMessage(99);
		JSONObject object = JSONHelper.string2json(Info);
		if (object != null && object.containsKey("menuid") && object.containsKey("optype")) {
			try {
				String mid = object.get("menuid").toString();
				int type = Integer.parseInt(object.get("optype").toString());
				if (type == 0) {
					result = SetRole(rid, mid);
				} else {
					result = DeleteMenuByRole(rid, mid);
				}
			} catch (Exception e) {
				nlogger.logout(e);
				result = resultMessage(99);
			}
		}
		return result;
	}

	/**
	 * 给菜单设置所属角色id
	 * 
	 * @project GrapeMenu
	 * @package interfaceApplication
	 * @file Menu.java
	 * 
	 * @param id
	 *            角色id
	 * @param mid
	 *            菜单id
	 * @return
	 *
	 */
	private String SetRole(String id, String mid) {
		long code = 0;
		String result = resultMessage(99);
		String[] mids = mid.split(",");
		try {
			if (mids.length == 1) {
				result = SetManager(id, mid);
			} else {
				String menuId = getMenuid(id, mids);
				String[] ids = menuId.split(",");
				for (String string : ids) {
					if (code == 0) {
						result = SetManager(id, string);
						code = (long) JSONHelper.string2json(result).get("errorcode");
					} else {
						result = resultMessage(99);
						break;
					}
				}
			}
		} catch (Exception e) {
			nlogger.logout(e);
			result = resultMessage(99);
		}
		return result;
	}

	/**
	 * 删除某角色下的菜单
	 * 
	 * @project GrapeMenu
	 * @package interfaceApplication
	 * @file Menu.java
	 * 
	 * @param rid
	 *            角色id
	 * @param mid
	 *            菜单id
	 * @return
	 *
	 */
	private String DeleteMenuByRole(String rid, String mid) {
		long code = 0;
		String result = resultMessage(99);
		String[] mids = mid.split(",");
		try {
			if (mids.length == 1) {
				result = Delete(rid, mid);
			} else {
				String menuId = getMenuid(rid, mids);
				String[] ids = menuId.split(",");
				for (String string : ids) {
					if (code == 0) {
						result = Delete(rid, string);
						code = (long) JSONHelper.string2json(result).get("errorcode");
					} else {
						result = resultMessage(99);
						break;
					}
				}
			}
		} catch (Exception e) {
			nlogger.logout(e);
			result = resultMessage(99);
		}
		return result;
	}

	private String Delete(String rid, String mid) {
		int code = 0;
		JSONObject object = getMenu(rid, mid);
		if (object == null) {
			return resultMessage(4);
		}
		try {
			String prvid = "";
			object = getPrv(mid);
			if (object != null) {
				prvid = object.get("prvid").toString();
				List<String> list = Str2List(prvid);
				if (list.size() != 1) {
					list.remove(rid);
					prvid = StringHelper.join(list);
				}
			}
			prvid = "{\"prvid\":\"" + prvid + "\"}";
			code = menus.eq("_id", new ObjectId(mid)).data(prvid).update() != null ? 0 : 99;
		} catch (Exception e) {
			nlogger.logout(e);
			code = 99;
		}
		return resultMessage(code, "删除成功");
	}

	/**
	 * 给菜单设置管理员
	 * 
	 * @project GrapeMenu
	 * @package interfaceApplication
	 * @file Menu.java
	 * 
	 * @param id
	 *            角色id
	 * @param mid
	 *            菜单id
	 * @return
	 *
	 */
	private String SetManager(String id, String mid) {
		int code = 99;
		JSONObject object = getMenu(id, mid);
		if (object != null) {
			return resultMessage(4);
		}
		try {
			String prvid = "";
			object = getPrv(mid);
			if (object != null) {
				prvid = object.get("prvid").toString();
				List<String> list = Str2List(prvid);
				list.add(id);
				prvid = StringHelper.join(list);
			}
			prvid = "{\"prvid\":\"" + prvid + "\"}";
			code = menus.eq("_id", new ObjectId(mid)).data(prvid).update() != null ? 0 : 99;
		} catch (Exception e) {
			nlogger.logout(e);
			code = 99;
		}
		return resultMessage(code, "设置管理员权限成功");
	}

	/**
	 * 用逗号分隔的字符串转换成list
	 * 
	 * @project GrapeMenu
	 * @package interfaceApplication
	 * @file Menu.java
	 * 
	 * @param iString
	 * @return
	 *
	 */
	private List<String> Str2List(String iString) {
		List<String> list = new ArrayList<String>();
		String[] strings = iString.split(",");
		for (String string : strings) {
			list.add(string);
		}
		return list;
	}

	/**
	 * 获取菜单id
	 * 
	 * @project GrapeMenu
	 * @package interfaceApplication
	 * @file Menu.java
	 * 
	 * @param id
	 *            角色id
	 * @param mids
	 *            菜单id
	 * @return
	 *
	 */
	private String getMenuid(String id, String[] mids) {
		List<String> list = new ArrayList<String>();
		for (String menuid : mids) {
			JSONObject object = getMenu(id, menuid);
			if (object == null) {
				list.add(menuid);
			}
		}
		return StringHelper.join(list);
	}

	/**
	 * 获取菜单所属角色id
	 * 
	 * @project GrapeMenu
	 * @package interfaceApplication
	 * @file Menu.java
	 * 
	 * @param id
	 * @return
	 *
	 */
	private JSONObject getPrv(String id) {
		JSONObject object = menus.eq("_id", new ObjectId(id)).field("prvid").find();
		return object != null ? object : null;
	}

	/**
	 * 根据菜单id和角色id，查询菜单
	 * 
	 * @project GrapeMenu
	 * @package interfaceApplication
	 * @file Menu.java
	 * 
	 * @param id
	 *            角色id
	 * @param prvid
	 *            菜单id
	 * @return
	 *
	 */
	private JSONObject getMenu(String id, String mid) {
		JSONObject object = menus.eq("_id", new ObjectId(mid)).like("prvid", id).find();
		return object != null ? object : null;
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
		case 3:
			msg = "登录信息已失效";
			break;
		case 4:
			msg = "该管理员已具备操作此菜单的权限";
			break;
		case 5:
			msg = "该管理员不具备操作此菜单的权限";
			break;
		default:
			msg = "其他操作异常";
			break;
		}
		return jGrapeFW_Message.netMSG(num, msg);
	}
}
