package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONObject;

import apps.appsProxy;
import authority.userDBHelper;
import database.db;
import esayhelper.JSONHelper;
import esayhelper.formHelper;
import esayhelper.formHelper.formdef;
import rpc.execRequest;

public class MenuModel {
	private static userDBHelper menus;
	private static formHelper form;

	static {
		menus = new userDBHelper("menu", (String) execRequest.getChannelValue("sid"));
		form = menus.getChecker();
	}

	public MenuModel() {
		form.putRule("name", formdef.notNull);
		form.putRule("prvid", formdef.notNull);
	}

	public db getdb() {
		return menus.bind(String.valueOf(appsProxy.appid()));
	}

	public JSONObject check(String info, HashMap<String, Object> map) {
		JSONObject object = AddMap(map, info);
		return !form.checkRuleEx(object) ? null : object;
	}

	/**
	 * 将map添加至JSONObject中
	 * 
	 * @param map
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private JSONObject AddMap(HashMap<String, Object> map, String Info) {
		JSONObject object = JSONHelper.string2json(Info);
		if (object != null) {
			if (map.entrySet() != null) {
				Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator.next();
					if (!object.containsKey(entry.getKey())) {
						object.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}
		return object;
	}
}
