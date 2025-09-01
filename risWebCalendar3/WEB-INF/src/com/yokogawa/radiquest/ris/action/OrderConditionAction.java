package com.yokogawa.radiquest.ris.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yokogawa.radiquest.ris.DBAccess.ByoutouMasterDataHome;
import com.yokogawa.radiquest.ris.DBAccess.CodeConvertDataHome;
import com.yokogawa.radiquest.ris.DBAccess.ExamRoomDataHome;
import com.yokogawa.radiquest.ris.DBAccess.KensaTypeDataHome;
import com.yokogawa.radiquest.ris.DBAccess.SectionMasterDataHome;
import com.yokogawa.radiquest.ris.DBAccess.StatusDefineDataHome;
import com.yokogawa.radiquest.ris.bean.OrderCondition;
import com.yokogawa.radiquest.ris.core.DBAccessException;
import com.yokogawa.radiquest.ris.servlet.Parameters;

public class OrderConditionAction extends FormAction {
	public static final String ALL_EXAM_ROOM_ID = Parameters.VALUE_EXAM_ROOM_ALL;

	public OrderConditionAction(HttpServletRequest request, HttpServletResponse response) {
		super(request, response);
	}

	public void getKensaTypeList(){
		List kensaTypeList = new ArrayList();
		KensaTypeDataHome ktdHome = new KensaTypeDataHome();

		HttpServletRequest request = getRequest();

		try {
			kensaTypeList = ktdHome.getKensaTypeList();
		} catch (DBAccessException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		request.setAttribute(Parameters.KENSA_TYPES, kensaTypeList);
	}

	public void getRIOrderList(){
		List riOrderList = new ArrayList();
		CodeConvertDataHome ccdHome = new CodeConvertDataHome();

		HttpServletRequest request = getRequest();

		try {
			riOrderList = ccdHome.getRIOrder();
		} catch (DBAccessException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		request.setAttribute(Parameters.RI_ORDER, riOrderList);
	}

	public void getKensaStatusList(){
		List kensaStatusList = new ArrayList();
		StatusDefineDataHome sdDataHome = new StatusDefineDataHome();

		HttpServletRequest request = getRequest();

		try {
			kensaStatusList = sdDataHome.getStatusDefines();
		} catch (DBAccessException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		request.setAttribute(Parameters.KENSA_STATUS, kensaStatusList);
	}

	public void getExamRoomList(){
		List examRoomList = new ArrayList();
		ExamRoomDataHome erdHome = new ExamRoomDataHome();

		HttpServletRequest request = getRequest();

		// 検査種別
		String kensaType = request.getParameter(Parameters.KENSA_TYPE);
		String[] kensaTypes = kensaType.split(":");

		try {
			examRoomList = erdHome.getExamRoomList(kensaTypes);
		} catch (DBAccessException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		request.setAttribute(Parameters.EXAM_ROOMS, examRoomList);
	}

	public void getSectionList() {
		List sectionList = new ArrayList();
		SectionMasterDataHome sectionHome = new SectionMasterDataHome();

		HttpServletRequest request = getRequest();

		try {
			sectionList = sectionHome.getSectionList();
		} catch (DBAccessException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		request.setAttribute(Parameters.SECTIONS, sectionList);
	}

	public void getByoutouList() {
		List byoutouList = new ArrayList();
		ByoutouMasterDataHome byoutouHome = new ByoutouMasterDataHome();

		HttpServletRequest request = getRequest();

		try {
			byoutouList = byoutouHome.getByoutouList();
		} catch (DBAccessException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		request.setAttribute(Parameters.BYOUTOUS, byoutouList);
	}

	public static OrderCondition getPageBean(HttpServletRequest request) {
		OrderCondition ocBean = (OrderCondition) request.getAttribute("ORDERCONDITIONBEAN");
		if(ocBean == null){
			ocBean = new OrderCondition();
		}
		return ocBean;
	}
}
