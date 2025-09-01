package com.yokogawa.radiquest.ris.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.yokogawa.radiquest.ris.bean.OrderDetail;

public class DataSort {
	public List GetOrderItemList(List odItemList) {
		sortDataList(odItemList);
		return odItemList;
	}

	public List GetOrderItemList2(ArrayList odItemList) {
		sortDataList2(odItemList);
		return odItemList;
	}

	private void sortDataList(List list) {
		Collections.sort(list, new DataComparator());
	}

	private void sortDataList2(ArrayList list) {
		Collections.sort(list, new DataComparator2());
	}

	public class DataComparator implements Comparator {
		DataComparator() {
			super();
		}

		public int compare(Object obj1, Object obj2) {
			int n1 = 0, n2 = 0;
			int ret = 0;
			/*
			 * 　　@ Koyama オーダ詳細表示項目showOrderで降順ソート(表示時に下から突っ込むため)
			 * showOrderには-(マイナス)の値も許可されている事に注意 showOrderが同値の時はnoが小さい方が後ろ
			 */
			if (obj1.getClass() == Configuration.OrderDetailItem.class
					&& obj2.getClass() == Configuration.OrderDetailItem.class) {
				n1 = ((Configuration.OrderDetailItem) obj1).getShowOrder();
				n2 = ((Configuration.OrderDetailItem) obj2).getShowOrder();

				if (n1 < n2) {
					ret = -1;
				} else if (n1 > n2) {
					ret = 1;
				}
			}
			return ret;
		}
	}

	public class DataComparator2 implements Comparator {
		DataComparator2() {
			super();
		}

		public int compare(Object obj1, Object obj2) {
			int n1 = 0, n2 = 0;
			int ret = 0;
			/*
			 * 　　@ Koyama オーダ詳細表示項目showOrderで降順ソート(表示時に下から突っ込むため)
			 * showOrderには-(マイナス)の値も許可されている事に注意 showOrderが同値の時はnoが小さい方が後ろ
			 */
			if (obj1.getClass() == OrderDetail.class
					&& obj2.getClass() == OrderDetail.class) {
				n1 = new Integer(((OrderDetail) obj1).getKensaStartTime_NoDisp())
						.intValue();
				n2 = new Integer(((OrderDetail) obj2).getKensaStartTime_NoDisp())
						.intValue();
				if(n1 == 999999){
					ret = -1;
				} else if (n1 < n2) {
					ret = -1;
				} else if (n1 > n2) {
					ret = 1;
				}
			}
			return ret;
		}
	}
}
