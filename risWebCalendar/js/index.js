// cookie名
var COOKIE_KENSA_TYPE = "kensaType";
var COOKIE_EXAM_ROOM  = "examRoom";
var COOKIE_EXAM_ROOMNAME ="examRoomName";
var COOKIE_TIME_FRAME = "timeFrame";
var COOKIE_KENSA_STATUS  = "kensaStatus";
var COOKIE_RI_ORDER = "riOrder";
var COOKIE_SECTION = "section";
var COOKIE_BYOUTOU = "byoutou";
var COOKIE_KANJA_NYUGAI = "kanjaNyugai";
var COOKIE_DENPYOU_NYUGAI = "denpyouNyugai";
var COOKIE_DISPLAY_TYPE = "displayType"; // 2012.01.12 Add Yk.Suzuki@CIJ 表示切替

// cookieの有効期間(10年)
var COOKIE_EXPIRES = 3650;

// 画面レイアウト情報
var CalendarOffsetHeight = 350;
var CalendarOffsetWidth = 340;
var OffsetWidth = 340;
var orderHedderOffset = 2;
var scrollbarWidth = 18;

// 検査室全室ID
var ALL_EXAM_ROOM_ID = "__ALL__";

// カレンダー表示開始日
var currentCalendarDate = new Date();

// カレンダーに表示している検査種別・検査室・RI区分・検査ステータス・時間枠・依頼科・病棟・患者入外・伝票入外
var searchKensaType = "";
var searchExamRoom = "";
var searchKensaStatus = "";
var searchRIOrder = "";
var searchTimeFrame = "";
var searchSection = "";
var searchByoutou = "";
var searchKanjaNyugai = "";
var searchDenpyouNyugai = "";
var searchDisplayType = ""; // 2012.01.12 Add Yk.Suzuki@CIJ 表示切替

// 検査種別
// RI区分
var RiOrder = function(name, id){
	this.name = name;
	this.id = id;
}

// データベースから読み込んだ検査種別の配列
var kensaTypeArray;
// データベースから読み込んだRI区分の配列
var riOrderArray;
// データベースから読み込んだ検査ステータスの配列
var kensaStatusArray;
// データベースから読み込んだ依頼科の配列
var sectionArray;
// データベースから読み込んだ病棟の配列
var byoutouArray;
// 患者入外の配列
var kanjaNyugaiArray;
// 伝票入外の配列
var denpyouNyugaiArray;
// 2014.05.07 Add T.Koudate@COSMO Start #2682 Cookie不正
// データベースから読み込んだ検査室の配列
var kensaRoomArrayGlobal;
// 2014.05.07 Add T.Koudate@COSMO End   #2682 Cookie不正

// テキストボックスに表示する検査種別
var kensaData = new Array();
// テキストボックスに表示する検査室
var roomData = new Array();
// テキストボックスに表示するRI区分
var riOrderData = new Array();
// テキストボックスに表示する検査ステータス
var kensaStatusData = new Array();
// テキストボックスに表示する依頼科
var sectionData = new Array();
// テキストボックスに表示する病棟
var byoutouData = new Array();
// テキストボックスに表示する患者入外
var kanjaNyugaiData = new Array();
// テキストボックスに表示する伝票入外
var denpyouNyugaiData = new Array();

// オーダ詳細の開始時間(時間枠はsearchTimeFrame)
var orderDetailTime = "";

// カレンダー更新時に自動選択する時間枠
// この変数にYYYYMMDDhhmmで日時を設定すると、カレンダー更新時にその時間枠が自動的に選択される。
// (注) 設定には setAutoSelectDateTime() を使用すること。
var autoSelectDateTime = "";

// 変更・削除対象RIS識別ID
var targetRisID = "";

// デバッグ用ログ
var logger = new Log4js.getLogger("console");
logger.setLevel(Log4js.Level.OFF);
logger.addAppender(new Log4js.ConsoleAppender());

// オーダ詳細の表示モード
var orderDetailMode = 0;
// 更新時のerromessage
// 変更後の時間枠に検査があった場合(患者未決定)
var hasOrder = "変更後の時間枠に検査がありますが、よろしいですか？";
// 変更後の時間枠に同じ患者の検査があった場合
var hasOrderOfSamePatient = "変更後の時間枠に同じ患者の検査がありますが、よろしいですか？";
// 変更後の時間枠に別の患者の検査があった場合
var hasOrderOfDiffPatient = "変更後の時間枠に別の患者の検査がありますが、よろしいですか？";

// カレンダー用の曜日リスト
var weekchars = new Array( "日", "月", "火", "水", "木", "金", "土" );

var htmlPath = "";

var selectedTd = null;

// ADD 2011.09.20 endo -begin-
// カレンダー、オーダー詳細で以前に以前選択されたもの
var beforeSelectCal = null;
var beforeSelectOrder = null;
// ADD 2011.09.20 endo -end-

// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
var COL_MIN_COUNT = 4;
// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更

// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更
var kensatypeID = null;
var kensaStartTime = null;
var status = null;
var risID = null;
var statusDb = null;
var kensaStartTimeDlg = null;
var kensaSitueDlg = null;
var kensaDateDlg = null;
var result = null;
var kensaSituTitle = null;
var kensaSituID = null;

// 予約時刻定義
var selectBox = {
    '09:00'     : '09:00',
    '09:30'     : '09:30',
    '10:00'     : '10:00',
    '10:30'     : '10:30',
    '11:00'     : '11:00',
    '11:30'     : '11:30',
    '12:00'     : '12:00',
    '12:30'     : '12:30',
    '13:00'     : '13:00',
    '13:30'     : '13:30',
    '14:00'     : '14:00',
    '14:30'     : '14:30',
    '15:00'     : '15:00',
    '15:30'     : '15:30',
    '16:00'     : '16:00',
    '16:30'     : '16:30',
    '17:00'     : '17:00',
    '17:30'     : '17:30',
    '18:00'     : '18:00'
};

// ステータス定義
var STATUS_UNREGISTERED			= 0;	// 未受付 
var STATUS_ISLATE				= 1;	// 遅刻
var STATUS_ISCALLING			= 2;	// 呼出中
var STATUS_ISREGISTERED			= 10;	// 受付済
var STATUS_INOPERATION			= 20;	// 実施中
var STATUS_REST					= 21;	// 保留
var STATUS_RECALLING			= 24;	// 再呼出
var STATUS_REREGISTERED			= 25;	// 再受付
var STATUS_ISFINISHED			= 90;	// 実施済
var STATUS_STOP					= 91;	// 中止
var STATUS_DELETE				= -9;	// 削除	
var STATUS_DELETE_SAVEPOINT		= 99;	// 削除ステータス検索条件保存位置(100桁目)	//


// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更

// DOM構築終了
$(function() {
	init();

	// Ajax通信設定
	$.ajaxSetup({
		"timeout" : 10000,
		"beforeSend" : function() {
			logger.trace("beforeSend");
		},
		"complete" : function() {
			logger.trace("complete");
		},
		"error" : function() {
			logger.trace("error");
			document.body.style.cursor = 'auto';
			alert("通信でエラーが発生しました。");
		},
		"success" : function() {
			logger.trace("success");
		}
	});

	// DatePickerの言語を日本語にする。
	$.datepicker.setDefaults($.extend($.datepicker.regional['ja']));

	var dialogWidth = 'auto';

	// 検査種別ダイアログボックスの初期化
	if (!$.support.style)
		dialogWidth = $('#kensaTypeDialog').width() * 1.5;
	$('#kensaTypeDialog').dialog({
		bgiframe: true,
		autoOpen: false,
		modal: true,
		width: dialogWidth,
		show: 'blind',
		hide: 'blind',
		draggable: false,
		dialogClass: 'kensaTypeDialog',
		buttons: {
			'OK': function() {
				var count = 0;
				var elements = $('#kensaTypeCheckBoxes table input:checkbox');
				elements.each(function(i, elem){
					if(elem.checked){
						count++;
					}
				});
				if(count == 0){
					alert("検査種別を選択してください。");
					return;
				}

				delete kensaData;
				kensaData = new Array();
				elements.each(function(i, elem){
					if(elem.checked){
						count++;
						kensaData.push({"name" : elem.name, "id" : elem.value});
					}
				});
				var str = "";
				var ids = ""
				for(i=0;i<kensaData.length;i++){
					str += kensaData[i].name;
					ids += kensaData[i].id;
					if(i!=kensaData.length-1) {
						str+= ", ";
						ids+= ":";
					}
				}
				// 検査種別が選択しなおされたら、選択された検査室をクリアする
				if($('#kensaTypeID').val() != ids){
					delete roomData;
					roomData = new Array();
					$('#kensaRoomText').val("");
					$('#kensaRoomID').val("");
				}
				$('#kensaTypeText').val(str);
				$('#kensaTypeID').val(ids);
				// 検査室データ作成
				changeExamRoom(ids);
				searchOrder();
				if(ids!=""){
					$('#openKensaRoomDialog').attr("disabled",false);
				} else {
					$('#openKensaRoomDialog').attr("disabled",true);
					$('#kensaRoomText').val("");
					$('#kensaRoomID').val("");
				}
				$(this).dialog('close');
			},
			// 「Cancel」ボタンがクリックされた時には、
			// ダイアログボックスを閉じる
			'Cancel': function() {
				$(this).dialog('close');
				$('.ui-dialog #OKButton').removeAttr('disabled');
				var okButton = getDialogButton(".kensaTypeDialog", "OK");
				if (okButton) okButton.removeAttr('disabled');
			}
		}
	});

	// idが「openKensaTypeDialog」のリンクがクリックされた時に、
	// ダイアログボックスを開くようにする
	$('#openKensaTypeDialog').bind('click', function() {
		var x = $('#kensaTypeText').position().left;
		var y = $('#kensaTypeText').height() + $('#kensaTypeText').position().top + 5;
		$('#kensaTypeDialog').dialog('option', 'position', [x,y]);
		var okButton = getDialogButton(".kensaTypeDialog", "OK");
		if (okButton) okButton.attr('disabled','disabled');
		var elements = $('#kensaTypeCheckBoxes input:checkbox');
		elements.each(function(j, elem){
			elem.checked = false;
		});
		for(i=0;i<kensaData.length;i++){
			elements.each(function(j, elem){
				if(elem.value == kensaData[i].id){
					elem.checked = true;
					if (okButton) okButton.removeAttr('disabled');
				}
			});
		}

		// ダイアログボックスを開く
		$('#kensaTypeDialog').dialog('open');
		// $('html :not(.ui-dialog)').bind("dblclick", closeKensaTypeDialog);
	});

	// 検査室ダイアログの初期化
	if (!$.support.style)
		dialogWidth = 600;
	$('#kensaRoomDialog').dialog({
		bgiframe: true,
		autoOpen: false,
		width: dialogWidth,
		modal: true,
		show: 'blind',
		hide: 'blind',
		draggable: false,
		buttons: {
			'OK': function() {
				var elements = $('#kensaRoomCheckBoxes table input:checkbox');
				delete roomData;
				roomData = new Array();
				elements.each(function(i, elem){
					if(elem.checked){
						roomData.push({"name" : elem.name, "id" : elem.value});
					}
				});
				var str = "";
				var ids = "";
				for(i=0;i<roomData.length;i++){
					str += roomData[i].name;
					ids += roomData[i].id;
					if(i!=roomData.length-1) {
						str+= ", ";
						ids+= ":";
					}
				}
				$('#kensaRoomText').val(str);
				$('#kensaRoomID').val(ids);
				$(this).dialog('close');
				searchOrder();
			},
			// 「Cancel」ボタンがクリックされた時には、
			// ダイアログボックスを閉じる
			'Cancel': function() {
				$(this).dialog('close');
			}
		}
	});

	// idが「openRoomTypeDialog」のリンクがクリックされた時に、
	// ダイアログボックスを開くようにする
	$('#openKensaRoomDialog').bind('click', function() {
		var x = $('#kensaRoomText').position().left;
		var y = $('#kensaRoomText').height() + $('#kensaRoomText').position().top + 5;
		$('#kensaRoomDialog').dialog('option','position',[x,y]);
		var elements = $('#kensaRoomCheckBoxes input:checkbox');
		elements.each(function(j, elem){
			elem.checked = false;
		});
		for(i=0;i<roomData.length;i++){
			elements.each(function(j, elem){
				if(elem.value == roomData[i].id){
					elem.checked = true;
				}
			});
		}
		// ダイアログボックスを開く

		$('#kensaRoomDialog').dialog('open');
	});

	// RI区分ダイアログの初期化
	if (!$.support.style)
		dialogWidth = 400;
	$('#riOrderDialog').dialog({
		bgiframe: true,
		autoOpen: false,
		width: dialogWidth,
		modal: true,
		show: 'blind',
		hide: 'blind',
		draggable: false,
		buttons: {
			'OK': function() {
				var elements = $('#riOrderCheckBoxes .riOrder');
				delete riOrderData;
				riOrderData = new Array();
				elements.each(function(i, elem){
					if(elem.checked){
						riOrderData.push({"name" : elem.name, "id" : elem.value});
					}
				});
				var str = "";
				var ids = ""
				for(i=0;i<riOrderData.length;i++){
					str += riOrderData[i].name;
					ids += riOrderData[i].id;
					if(i!=riOrderData.length-1) {
						str+= ", ";
						ids+= ":";
					}
				}
				$('#riOrderText').val(str);
				$('#riOrderID').val(ids);
				$(this).dialog('close');
				searchOrder();
			},
			// 「Cancel」ボタンがクリックされた時には、
			// ダイアログボックスを閉じる
			'Cancel': function() {
				$(this).dialog('close');
			}
		}
	});
	// idが「openRiOrderTypeDialog」のリンクがクリックされた時に、
	// ダイアログボックスを開くようにする
	$('#openRiOrderDialog').bind('click', function() {
		var x = $('#riOrderText').position().left;
		var y = $('#riOrderText').height() + $('#riOrderText').position().top + 5;
		$('#riOrderDialog').dialog('option','position',[x,y]);
		var elements = $('#riOrderCheckBoxes input:checkbox');
		elements.each(function(j, elem){
			elem.checked = false;
		});
		for(i=0;i<riOrderData.length;i++){
			elements.each(function(j, elem){
				if(elem.value == riOrderData[i].id){
					elem.checked = true;
				}
			});
		}
		// ダイアログボックスを開く
		$('#riOrderDialog').dialog('open');
	});

	// 検査ステータスダイアログの初期化
	if (!$.support.style)
		dialogWidth = 400;
	$('#kensaStatusDialog').dialog({
		bgiframe: true,
		autoOpen: false,
		width: dialogWidth,
		modal: true,
		show: 'blind',
		hide: 'blind',
		draggable: false,
		buttons: {
			'OK': function() {
				var elements = $('#kensaStatusCheckBoxes .kensaStatus');
				delete kensaStatusData;
				kensaStatusData = new Array();
				elements.each(function(i, elem){
					if(elem.checked){
						kensaStatusData.push({"name" : elem.name, "id" : elem.value});
					}
				});
				var str = "";
				var ids = ""
				for(i=0;i<kensaStatusData.length;i++){
					str += kensaStatusData[i].name;
					ids += kensaStatusData[i].id;
					if(i!=kensaStatusData.length-1) {
						str+= ", ";
						ids+= ":";
					}
				}
				$('#kensaStatusText').val(str);
				$('#kensaStatusID').val(ids);
				$(this).dialog('close');
				searchOrder();
			},
			// 「Cancel」ボタンがクリックされた時には、
			// ダイアログボックスを閉じる
			'Cancel': function() {
				$(this).dialog('close');
			}
		}
	});
	// idが「openKensaStatusTypeDialog」のリンクがクリックされた時に、
	// ダイアログボックスを開くようにする
	$('#openKensaStatusDialog').bind('click', function() {
		var x = $('#kensaStatusText').position().left;
		var y = $('#kensaStatusText').height() + $('#kensaStatusText').position().top + 5;
		$('#kensaStatusDialog').dialog('option','position',[x,y]);
		var elements = $('#kensaStatusCheckBoxes input:checkbox');
		elements.each(function(j, elem){
			elem.checked = false;
		});
		for(i=0;i<kensaStatusData.length;i++){
			elements.each(function(j, elem){
				if(elem.value == kensaStatusData[i].id){
					elem.checked = true;
				}
			});
		}
		// ダイアログボックスを開く
		$('#kensaStatusDialog').dialog('open');
	});

	// 依頼科ダイアログの初期化
	if (!$.support.style)
		dialogWidth = $('#sectionDialog').width() * 1.5;
	$('#sectionDialog').dialog({
		bgiframe: true,
		autoOpen: false,
		width: dialogWidth,
		modal: true,
		show: 'blind',
		hide: 'blind',
		draggable: false,
		buttons: {
			'OK': function() {
				var elements = $('#sectionDialog .section');
				delete sectionData;
				sectionData = new Array();
				elements.each(function(i, elem){
					if(elem.checked){
						sectionData.push({"name" : elem.name, "id" : elem.value});
					}
				});
				var str = "";
				var ids = ""
				for(i = 0; i < sectionData.length; i++){
					str += sectionData[i].name;
					ids += sectionData[i].id;
					if(i != sectionData.length - 1) {
						str+= ", ";
						ids+= ":";
					}
				}
				$('#sectionText').val(str);
				$('#sectionID').val(ids);
				$(this).dialog('close');
				searchOrder();
			},
			// 「Cancel」ボタンがクリックされた時には、
			// ダイアログボックスを閉じる
			'Cancel': function() {
				$(this).dialog('close');
			}
		}
	});
	// idが「openSectionDialog」のリンクがクリックされた時に、
	// ダイアログボックスを開くようにする
	$('#openSectionDialog').bind('click', function() {
		var x = $('#sectionText').position().left + $('#SearchBlockCenter').position().left;
		var y = $('#sectionText').height() + $('#sectionText').position().top + 5 + $('#SearchBlockCenter').position().top;
		$('#sectionDialog').dialog('option','position',[x,y]);
		var elements = $('#sectionCheckBoxes input:checkbox');
		elements.each(function(j, elem){
			elem.checked = false;
		});
		for(i=0;i<sectionData.length;i++){
			elements.each(function(j, elem){
				if(elem.value == sectionData[i].id){
					elem.checked = true;
				}
			});
		}
		// ダイアログボックスを開く
		$('#sectionDialog').dialog('open');
	});

	// 病棟ダイアログの初期化
	if (!$.support.style)
		dialogWidth = $('#byoutouDialog').width() * 1.5;
	$('#byoutouDialog').dialog({
		bgiframe: true,
		autoOpen: false,
		width: dialogWidth,
		modal: true,
		show: 'blind',
		hide: 'blind',
		draggable: false,
		buttons: {
			'OK': function() {
				var elements = $('#byoutouDialog .byoutou');
				delete byoutouData;
				byoutouData = new Array();
				elements.each(function(i, elem){
					if(elem.checked){
						byoutouData.push({"name" : elem.name, "id" : elem.value});
					}
				});
				var str = "";
				var ids = ""
				for(i=0;i<byoutouData.length;i++){
					str += byoutouData[i].name;
					ids += byoutouData[i].id;
					if(i != byoutouData.length - 1) {
						str+= ", ";
						ids+= ":";
					}
				}
				$('#byoutouText').val(str);
				$('#byoutouID').val(ids);
				$(this).dialog('close');
				searchOrder();
			},
			// 「Cancel」ボタンがクリックされた時には、
			// ダイアログボックスを閉じる
			'Cancel': function() {
				$(this).dialog('close');
			}
		}
	});
	// idが「openByoutouDialog」のリンクがクリックされた時に、
	// ダイアログボックスを開くようにする
	$('#openByoutouDialog').bind('click', function() {
		var x = $('#byoutouText').position().left + $('#SearchBlockCenter').position().left;
		var y = $('#byoutouText').height() + $('#byoutouText').position().top + 5 + $('#SearchBlockCenter').position().top;
		$('#byoutouDialog').dialog('option','position',[x,y]);
		var elements = $('#byoutouCheckBoxes input:checkbox');
		elements.each(function(j, elem){
			elem.checked = false;
		});
		for(i=0;i<byoutouData.length;i++){
			elements.each(function(j, elem){
				if(elem.value == byoutouData[i].id){
					elem.checked = true;
				}
			});
		}
		// ダイアログボックスを開く
		$('#byoutouDialog').dialog('open');
	});

	// 患者入外ダイアログの初期化
	if (!$.support.style)
		dialogWidth = 250;
	$('#kanjaNyugaiDialog').dialog({
		bgiframe: true,
		autoOpen: false,
		width: dialogWidth,
		modal: true,
		show: 'blind',
		hide: 'blind',
		draggable: false,
		buttons: {
			'OK': function() {
				var elements = $('#kanjaNyugaiDialog .kanjaNyugai');
				delete kanjaNyugaiData;
				kanjaNyugaiData = new Array();
				elements.each(function(i, elem){
					if(elem.checked){
						kanjaNyugaiData.push({"name" : elem.name, "id" : elem.value});
					}
				});
				var str = "";
				var ids = ""
				for(i=0;i<kanjaNyugaiData.length;i++){
					str += kanjaNyugaiData[i].name;
					ids += kanjaNyugaiData[i].id;
					if(i!=kanjaNyugaiData.length-1) {
						str+= ", ";
						ids+= ":";
					}
				}
				$('#kanjaNyugaiText').val(str);
				$('#kanjaNyugaiID').val(ids);
				$(this).dialog('close');
				searchOrder();
			},
			// 「Cancel」ボタンがクリックされた時には、
			// ダイアログボックスを閉じる
			'Cancel': function() {
				$(this).dialog('close');
			}
		}
	});
	// idが「openKanjaNyugaiDialog」のリンクがクリックされた時に、
	// ダイアログボックスを開くようにする
	$('#openKanjaNyugaiDialog').bind('click', function() {
		var x = $('#kanjaNyugaiText').position().left + $('#SearchBlockCenter').position().left;
		var y = $('#kanjaNyugaiText').height() + $('#kanjaNyugaiText').position().top + 5 + $('#SearchBlockCenter').position().top;
		$('#kanjaNyugaiDialog').dialog('option','position',[x,y]);
		var elements = $('#kanjaNyugaiCheckBoxes input:checkbox');
		elements.each(function(j, elem){
			elem.checked = false;
		});
		for(i=0;i<kanjaNyugaiData.length;i++){
			elements.each(function(j, elem){
				if(elem.value == kanjaNyugaiData[i].id){
					elem.checked = true;
				}
			});
		}
		// ダイアログボックスを開く
		$('#kanjaNyugaiDialog').dialog('open');
	});

	// 伝票入外ダイアログの初期化
	if (!$.support.style)
		dialogWidth = 250;
	$('#denpyouNyugaiDialog').dialog({
		bgiframe: true,
		autoOpen: false,
		width: dialogWidth,
		modal: true,
		show: 'blind',
		hide: 'blind',
		draggable: false,
		buttons: {
			'OK': function() {
				var elements = $('#denpyouNyugaiDialog .denpyouNyugai');
				delete denpyouNyugaiData;
				denpyouNyugaiData = new Array();
				elements.each(function(i, elem){
					if(elem.checked){
						denpyouNyugaiData.push({"name" : elem.name, "id" : elem.value});
					}
				});
				var str = "";
				var ids = ""
				for(i=0;i<denpyouNyugaiData.length;i++){
					str += denpyouNyugaiData[i].name;
					ids += denpyouNyugaiData[i].id;
					if(i!=denpyouNyugaiData.length-1) {
						str+= ", ";
						ids+= ":";
					}
				}
				$('#denpyouNyugaiText').val(str);
				$('#denpyouNyugaiID').val(ids);
				$(this).dialog('close');
				searchOrder();
			},
			// 「Cancel」ボタンがクリックされた時には、
			// ダイアログボックスを閉じる
			'Cancel': function() {
				$(this).dialog('close');
			}
		}
	});
	// idが「openDenpyouNyugaiDialog」のリンクがクリックされた時に、
	// ダイアログボックスを開くようにする
	$('#openDenpyouNyugaiDialog').bind('click', function() {
		var x = $('#denpyouNyugaiText').position().left + $('#SearchBlockCenter').position().left;
		var y = $('#denpyouNyugaiText').height() + $('#denpyouNyugaiText').position().top + 5 + $('#SearchBlockCenter').position().top;
		$('#denpyouNyugaiDialog').dialog('option','position',[x,y]);
		var elements = $('#denpyouNyugaiCheckBoxes input:checkbox');
		elements.each(function(j, elem){
			elem.checked = false;
		});
		for(i=0;i<denpyouNyugaiData.length;i++){
			elements.each(function(j, elem){
				if(elem.value == denpyouNyugaiData[i].id){
					elem.checked = true;
				}
			});
		}
		// ダイアログボックスを開く
		$('#denpyouNyugaiDialog').dialog('open');
	});
	
	// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更
	// 予約時刻、予定検査室ダイアログの初期化
	if (!$.support.style)
		dialogWidth = 250;
	$('#timelaboratoryDialog').dialog({
		bgiframe: true,
		autoOpen: false,
		width: 340,
		height: 230,
		modal: true,
		show: 'blind',
		hide: 'blind',
		draggable: false,
		buttons: {
			'OK': function() {
				
				if ( $('#cmbReservationTimeDlg').is(':disabled') === false ) {
					var selectDate = $('.select2-selection__rendered').text();
					if(!selectDate.match(/^([01]?[0-9]|2[0-3]):([0-5][0-9])$/)){
						alert("正しい日付を入力して下さい。")
						return;
					}
				}
				
				// 画面ステータスとDBのステータスに変更がないかチェック
	        	chekStatusOk(risID);
	        	
	        	setTimeout(function() { 
	        		
	        		
		        	// 画面のステータスとDBのステータスを比較する
					if (status != statusDb) {
						alert("ステータスが変更されています");
						return;
					}
					
					// ステータス：未受付(0)、遅刻(1)、呼出し(2)、受済(10)以外は使用不可
					if (status != STATUS_UNREGISTERED && status != STATUS_ISLATE && status != STATUS_ISCALLING && status != STATUS_ISREGISTERED) 
					{
						alert("未受付(0)、遅刻(1)、呼出し(2)、受済(10)以外は変更出来ません");
						return;
					}
						
					// 予約時刻を取得
					kensaStartTimeDlg = $("#cmbReservationTimeDlg").val();
					
					// 予定検査室を取得
					kensaSitueDlg = $("#cmbPlansLaboratoryDlg").val();
					
					 //ACCESSINFOにアクセスする
					selectAccessinfo(risID,kensaStartTimeDlg,kensaSitueDlg,kensaDateDlg);
					
	        	}, 10);
				
			},
			// 「Cancel」ボタンがクリックされた時には、
			// ダイアログボックスを閉じる
			'Cancel': function() {
				$(this).dialog('close');
			}
		},
		open: function(event) {

			// コンボボックスのレイアウト調整
			$("#cmbReservationTimeDlg").attr('disabled', false);
    		$("#cmbPlansLaboratoryDlg").attr('disabled', false);
    		$("#cmbPlansLaboratoryDlg").css('width', 150);
    		$(".dlg-col1").css('margin-right', 30);
    		$(".dlg-col2").css('margin-right', 12);
    		$("#PlansLaboratory").css('margin-top', 10);

			// 予約時刻
    		$('#cmbReservationTimeDlg').select2({
    			width:150,
    		    tags: true,
    		    createTag: function( obj ) {
    		        // ここでreturnを実行しないと入力できなくなるので注意
    		        return {
    		            id:   obj.term,
    		            text: obj.term,
    		            // この値があるかどうかで判断します
    		            isNewFlag: true
    		        };
    		    }
    		})

			// 画面のステータスとDBのステータスを比較する
			if (status != statusDb) {
				alert("ステータスが変更されています");
				$(".select2-selection").attr('disabled', true);
    			$("#cmbPlansLaboratoryDlg").attr('disabled', true);
				return;
			}
					
			// ステータス：未受付(0)、遅刻(1)、呼出し(2)、受済(10)以外は使用不可
			if (status != STATUS_UNREGISTERED && status != STATUS_ISLATE && status != STATUS_ISCALLING && status != STATUS_ISREGISTERED) {
				alert("未受付(0)、遅刻(1)、呼出し(2)、受済(10)以外は変更出来ません");
				$(".select2-selection").attr('disabled', true);
				$("#cmbPlansLaboratoryDlg").attr('disabled', true);
				return;
				}
				},
    	close: function(event) {
    		$(".select2-selection").removeAttr('disabled');
    	}
	});
	// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更
	
	// 検査種別
	setKensaType("kensaType");

	// 検査室
	setExamRoom();

	// 時間枠
	setTimeFrame();

	// カレンダーの読み込み
	changeCalendar(currentCalendarDate);

	// オーダ詳細
	showOrderDetail(""); // カレンダー読み込みで更新される
	
});

// ブラウザのリサイズ
$(window).resize(function() {
	resizeCalendar();
	resizeOrderDetail();
});

function resizeCalendar() {
	// ウィンドウの幅、高さを取得する。
	var windowHeight = getWindowHeight();
	var windowWidth = getWindowWidth();

	if (windowHeight == 0 || windowWidth == 0) return;

	// カレンダーのデータ部分
	var timeblock = document.getElementById("timeblock");
	if (timeblock == null)
		return;

	var calendarArea = document.getElementById("calendarArea");
	// 時間とオーダー詳細テーブルを表示する領域の高さを決定する。
	var marginHeight = ($("#CalendarBlock").outerHeight(true) - $("#CalendarBlock").height());
	var timeBlockHeight = $("#CalendarBlock").height() - $("#dateblock").height() - marginHeight;
	if (timeBlockHeight > 0) {
		timeblock.style.height = new String(timeBlockHeight) + "px";
	}

	var tables = timeblock.getElementsByTagName("table");
	if (tables == null || tables.length != 1) return;

	var orderTable = tables[0];

	// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL003 検査室毎画面　縦表示
	var pair=location.search.substring(1);
	if (pair == "action=calendar") {
		// テーブルの横幅を決定する
		var tableWidth = calendarArea.clientWidth - 20;
		if(tableWidth <= 0){
			tableWidth = 0;
		}
		orderTable.style.width = new String(tableWidth) + "px";
		// テーブル表示領域の幅を決定する。テーブル幅にスクロールバーの幅を追加したサイズとする。
		timeblock.style.width = new String(tableWidth + scrollbarWidth) + "px";

		var dateblock = document.getElementById("dateblock");
		var dateTables = dateblock.getElementsByTagName("table");
		var dateTable = dateTables[0];
		dateTable.style.width = new String(tableWidth) + "px";

		timeblock.style.borderColor = "#000";
		if (timeblock.clientHeight > orderTable.clientHeight) {
			timeblock.style.borderColor = "#FFF";
		}
	} else {
		// テーブルの横幅を決定する
		// 2015.02.27 Mod S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
		var blockWidth = calendarArea.clientWidth - 20;
		var tableWidth = blockWidth;
		// 列数(ヘッダ列を除く)
		var colCount = orderTable.rows[1].cells.length -1;
		//デフォルト列数より多いときテーブル横幅を拡張
		if (colCount > COL_MIN_COUNT){
			tableWidth = blockWidth / COL_MIN_COUNT * colCount;
		}
		//var tableWidth = calendarArea.clientWidth - 20;
		// 2015.02.27 Mod S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更
		if(tableWidth <= 0){
			tableWidth = 0;
		}
		orderTable.style.width = new String(tableWidth) + "px";
		// テーブル表示領域の幅を決定する。テーブル幅にスクロールバーの幅を追加したサイズとする。
		// 2015.02.27 Mod S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
		timeblock.style.width = new String(blockWidth + scrollbarWidth) + "px";
		//timeblock.style.width = new String(tableWidth + scrollbarWidth) + "px";
		// 2015.02.27 Mod S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更

		var dateblock = document.getElementById("dateblock");
		var dateTables = dateblock.getElementsByTagName("table");
		var dateTable = dateTables[0];
		// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
		dateblock.style.width = new String(blockWidth) + "px";
		// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更
		dateTable.style.width = new String(tableWidth) + "px";

		timeblock.style.borderColor = "#000";
		if (timeblock.clientHeight > orderTable.clientHeight) {
			timeblock.style.borderColor = "#FFF";
		}
	}
	// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL003 検査室毎画面　縦表示

	// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL003 検査室毎画面　縦表示
	// 検査室毎縦表示のwidthを設定する。
	if (pair == "action=kensasitu_h") {
		var columnNum = $("table.datatable")[0].rows[0].cells.length;
		var width_h = 170 * (columnNum - 1) + 40
		$("table.datatable").width(width_h);
		$("table.timetable").width(width_h);
	}
	// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL003 検査室毎画面　縦表示

}

function resizeOrderDetail() {
	// ウィンドウの幅、高さを取得する。
	var windowWidth = getWindowWidth();
	if (windowWidth == 0) return;

	var orderDetailSection = document.getElementById("OrderDetailSection");
	if (orderDetailSection == null) return;

	// div(表示領域)の幅を決定
	var pDivWidth = windowWidth - OffsetWidth + scrollbarWidth;
	if(pDivWidth <= 0){
		pDivWidth = 0;
	}

	// headerDIVの幅を決定
	var headerDivWidth = windowWidth;
	if(headerDivWidth <= 0){
		headerDivWidth = 0;
	}

	orderDetailSection.style.width = new String(headerDivWidth - scrollbarWidth) + "px";

	var orderDetailHead = document.getElementById("OrderDetailHead");
	if (orderDetailHead == null) return;
	orderDetailHead.style.width = new String(headerDivWidth + orderHedderOffset - scrollbarWidth) + "px";

	// listDIVの幅を決定
	var orderDetailList = document.getElementById("OrderDetailList");
	if (orderDetailList == null) return;
	orderDetailList.style.width = new String(headerDivWidth) + "px";

	// 総カラム幅を取得
	var allColumnWidth = $("#tableWidth").val();
	// カラム数を取得
	var columnCnt = $("#columnCnt").val();
	if (allColumnWidth && columnCnt){
		// テーブルのサイズを取得
		var tableWidth = parseInt(allColumnWidth, 10) + parseInt(columnCnt, 10);
		// DIVの幅がテーブル幅より大きい時はdummyをセットして調整
		if (tableWidth < pDivWidth) {
			var dummyWidth = pDivWidth - tableWidth - scrollbarWidth;
			var dummyTH = document.getElementById("dummyTH");
			if (dummyTH) {
				dummyTH.style.width = new String (dummyWidth);
				var dataCnt = $("#dataCnt").val();
				if (dataCnt) {
					for (var i = 0; i < parseInt(dataCnt, 10); i++) {
						var dummyId = "dummyTD" + i;
						var dummyTD = document.getElementById(dummyId);
						dummyTD.style.width = new String(dummyWidth);
					}
				}
			}
		}
	}
}

/*
 * 一覧表示エリアの最初の行を選択状態にする。
 */
function selectFirstRowOrderDetail() {
	$("#OrderDetailList tr").each(function() {
		selectOrder(this);
		return false;
	});
}

var leftPos = 0;
function hScrollOrderDetail() {
	$("#OrderDetailHead").scrollLeft($("#OrderDetailList").scrollLeft());
}

function getWindowHeight() {
	var height = 0;
	if (window.innerHeight != null && window.innerHeight > 0) {
		height = window.innerHeight;
	} else if(document.compatMode=='CSS1Compat'){
		// 標準モードの時
		height = document.documentElement.clientHeight;
	} else{
		// 互換モードの時
		height = document.body.clientHeight;
	}

	return height;
}

function getWindowWidth() {
	var width = 0;
	if (window.innerWidth != null && window.innerWidth > 0) {
		width = window.innerWidth;
	} else if(document.compatMode=='CSS1Compat'){
		// 標準モードの時
		width = document.documentElement.clientWidth;
	} else{
		// 互換モードの時
		width = document.body.clientWidth;
	}
	return width;
}

function setKensaTypeAjax(elementIdName) {
	var selectElement = document.getElementById(elementIdName);
	if (selectElement == null)
		return;

	var prevKensaType = $.cookie(COOKIE_KENSA_TYPE);

	if (prevKensaType == null)
		prevKensaType = "";

	selectElement.options.length = 0;
	var elem = document.createElement("option");
	elem.text = "";
	elem.value = "";
	selectElement.options[selectElement.options.length] = elem;

	var url = "ReservationServlet?action=kensaType" + addPinParameter();

	logger.trace(url);
//	document.body.style.cursor = 'wait';
	$.getJSON(url, function(json) {
		var kensaTypes = json;
		$.each(kensaTypes, function(i, kensaType) {
			elem = document.createElement("option");
			elem.text = kensaType.name;
			elem.value = kensaType.id;
			selectElement.options[selectElement.options.length] = elem;

			if (prevKensaType == elem.value)
				selectElement.selectedIndex = selectElement.options.length - 1;
		});
//		document.body.style.cursor = 'auto';
	});
}

function setKensaType(elementIdName) {
	var selectElement = document.getElementById(elementIdName);
	if (selectElement == null)
		return;

	var prevKensaType = $.cookie(COOKIE_KENSA_TYPE);
	if (prevKensaType == null)
		prevKensaType = "";

	var index = 0;
	for (var i = 0; i < selectElement.options.length; i++) {
		var option = selectElement.options[i];
		if (option.value == prevKensaType) {
			index = i;
			break;
		}
	}
	selectElement.selectedIndex = index;
}

function setExamRoom() {
	var selectElement = document.getElementById("examRoom");
	if (selectElement == null)
		return;

	var prevExamRoom = $.cookie(COOKIE_EXAM_ROOM);

	selectElement.options.length = 0;

	// 2014.05.07 Add T.Koudate@COSMO Start #2682 Cookie不正
	var oldIdList = prevExamRoom.val().split(":");
	var newIdList = "";
	var newNameList = "";
	// 2014.05.07 Add T.Koudate@COSMO End   #2682 Cookie不正

	var url = "ReservationServlet?action=examRoom";
	var kensaType = $.cookie(COOKIE_KENSA_TYPE);
	if (kensaType != null)
		url += "&kensaType=" + kensaType;
	url += "&all=true";
	url += addPinParameter();

	logger.trace(url);
//	document.body.style.cursor = 'wait';
	$.getJSON(url, function(json) {
		var examRooms = json;
		var index = -1;
		$.each(examRooms, function(i, examRoom) {
			var elem = document.createElement("option");
			elem.text = examRoom.name;
			elem.value = examRoom.id;
			selectElement.options[selectElement.options.length] = elem;

			if (examRoom.id == prevExamRoom)
				index = selectElement.options.length - 1;

			// 2014.05.07 Add T.Koudate@COSMO Start #2682 Cookie不正
			// 最新DB情報とCOOKIEの内容が一致しているもののみを取得
			for (var j=0; j<oldIdList.length; j++) {
				if (examRoom.id == oldIdList[j].id) {
					newIdList   += examRoom.id   + ":";
					newNameList += examRoom.name + ",";
				}
			}
			// 2014.05.07 Add T.Koudate@COSMO End   #2682 Cookie不正
		});

		// 2014.05.07 Add T.Koudate@COSMO Start #2682 Cookie不正
		$('#kensaRoomID').val(newIdList.substr(0, newIdList.length-1));
		$('#kensaRoomText').val(newNameList.substr(0, newNameList.length-1));
		// 2014.05.07 Add T.Koudate@COSMO End   #2682 Cookie不正

		selectElement.selectedIndex = index;

		searchOrder();

//		document.body.style.cursor = 'auto';
	});
}

function setTimeFrame() {
	var selectElement = document.getElementById("timeFrame");
	if (selectElement == null)
		return;

	var prevTimeFrame = $.cookie(COOKIE_TIME_FRAME);
	if (prevTimeFrame == null || prevTimeFrame == "")
		prevTimeFrame = "60";

	var index = 0;
	for (var i = 0; i < selectElement.options.length; i++) {
		var option = selectElement.options[i];
		if (option.value == prevTimeFrame) {
			index = i;
			break;
		}
	}
	selectElement.selectedIndex = index;

	searchTimeFrame = prevTimeFrame;
}

function searchKensaTypeChanged(select) {
	var kensaType = select.options[select.selectedIndex].value;
	// changeExamRoom("examRoom", kensaType, true);
}

function changeExamRoom(kensaType) {
	var url = "ReservationServlet?action=examRoom"
		+ "&kensaType=" + kensaType;

	url += addPinParameter();
	logger.trace(url);
//	document.body.style.cursor = 'wait';
	$.ajaxSetup({ async: false }); // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
	$.getJSON(url, function(json) {
		// データベースから読み込んだ検査室の配列
		kensaRoomArrayGlobal = json; // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
		var kensaRoomArray = json;
//		document.body.style.cursor = 'auto';
		insertKensaRoom(kensaRoomArray);
	});
	$.ajaxSetup({ async: true }); // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
}

// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更
// 予約時刻・検査室変更ダイアログ
function changeExamRoomDlg(kensaType) {
	
	var url = "ReservationServlet?action=examRoom"
		+ "&kensaType=" + kensaType;

	url += addPinParameter();
	logger.trace(url);
	$.getJSON(url, function(json) {
		// データベースから読み込んだ検査室の配列
		var kensaRoomArray = json;
		insertKensaRoomDlg(kensaRoomArray);
	});
}

// 画面ステータスとDBステータスチェック
function chekStatus(risID) {

	var url = "ReservationServlet?action=status"
		+ "&risID=" + risID;

	url += addPinParameter();
	logger.trace(url);

	$.getJSON(url, function(json) {
		// データベースから読み込んだステータス
		
		statusDb = json.status;
		
	});
}

function chekStatusOk(risID) {

	var url = "ReservationServlet?action=status"
		+ "&risID=" + risID;

	url += addPinParameter();
	logger.trace(url);

	$.getJSON(url, function(json) {
		// データベースから読み込んだステータス
		
		statusDb = json.status;
		
			
	});
}

// ACCESSINFOにアクセスする
function selectAccessinfo(risID,kensaStartTimeDlg,kensaSitueDlg,kensaDateDlg) {

	var url = "ReservationServlet?action=accessinfo"
		+ "&risID=" + risID
		+ "&status=" + status
		+ "&kensaStartTime=" + kensaStartTimeDlg
		+ "&KensaSituList=" + kensaSitueDlg
		+ "&kensaDate=" + kensaDateDlg;

	url += addPinParameter();
	logger.trace(url);

	$.getJSON(url, function(json) {
		// データベースから読み込んだステータス
		
		result = json.result;
		
		if (result != null) {
			if (result == 'OK') {
				$("#timelaboratoryDialog").dialog('close');
			} else {
				alert(result);
				$("#timelaboratoryDialog").dialog('close');
			}
		}
		
	});
	
	//alert(result);
	
}
// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更

function initCalendarItem() {
	// 日付エリア
	var dateblock = document.getElementById("dateblock");
	if (dateblock == null)
		return;
	var  divNodes = dateblock.getElementsByTagName("div");
	for (var i = 0; i < divNodes.length; i++) {
		var div = divNodes[i];
		if(!$(div).hasClass("date"))
			continue;

		// 日付エリアクリック
		var parentTD = div.parentNode;
		parentTD.onclick = function() {
			// 選択済みなら何もしない。
			if ($(this).hasClass("selected")) {
				return;
			}

			// 選択状態クリア
			selectedAllClear();

			// 選択状態にする。
			if (!$(this).hasClass("selected")) {
				$(this).addClass("selected");
			}

			// 選択された日付の列番号取得
			var dateColNum = $(this).find("span.dateColNum").text();

			// 同じ列番号のrisIDを取得
			var risIDParam="";
			$("td").find("div.detailData").each(function() {
				if($(this).find("span.valColNum").text() == dateColNum){
					risIDParam += $(this).find("span.risID").text() + ":";
				}
			});
			risIDParam = risIDParam.substring(0, risIDParam.length);

			// 詳細表示エリアをクリアする。
			showIraiDetail("");

			// 一覧表示エリアを更新する。
			showOrderDetail(risIDParam);
			showIraiDetail(risIDParam);
		}
	}

	// 時刻・枠エリア
	var timeblock = document.getElementById("timeblock");
	if (timeblock == null)
		return;
	var divNodes = timeblock.getElementsByTagName("div");
	for (var i = 0; i < divNodes.length; i++) {
		var div = divNodes[i];
		if (!$(div).hasClass("data"))
			continue;

		// 枠エリアクリック
		var parentTD = div.parentNode;
		parentTD.onclick = function() {
			// 選択済みなら何もしない。
			if ($(this).hasClass("selected")) {
				return;
			}

			// いったん、全ての選択状態をクリアする
			selectedAllClear();

			// 選択状態にする。
			if (!$(this).hasClass("selected")) {
				$(this).addClass("selected");
			}
			selectedTd = this;

			// 枠リストを表示する。
			showDetailData(this);

			// 選択枠内のRIS識別IDを取得する。
			var risIDParam="";
			$(this).find("span.risID").each(function() {
				risIDParam += $(this).text() + ":";
			});
			risIDParam = risIDParam.substring(0, risIDParam.length-1);

			// 一覧表示エリアを更新する。
			showOrderDetail(risIDParam);

			if(risIDParam.indexOf(":") == -1){
				// 詳細表示エリアを表示する。
				showIraiDetail(risIDParam);

				// 同セル内の全ての枠リストを非選択状態にする。
				$(this).find("div.detailData").parent().parent().find("div.detailData").each(function() {
					$(this).find("div.detailData").removeClass("selectedDetailData");
				});

				// クリックした枠リストを選択状態にする。
				$(this).find("div.detailData").addClass("selectedDetailData");
			}else{
				// 詳細表示エリアをクリアする。
				showIraiDetail("");
			}
		}

		// 枠リスト
		$(div).find("div.detailData").each(function() {
			// 枠リストクリック
			$(this).bind("click", function() {
				// 選択済みなら何もしない。
				if ($(this).hasClass("selectedDetailData")) {
					return;
				}

				// DEL 2011.09.20 endo -begin-
				// 同セル内の全ての枠リストを非選択状態にする。
			//	$(this).parent().parent().find("div.detailData").each(function() {
			//		$(this).removeClass("selectedDetailData");
			//	});

				// クリックした枠リストを選択状態にする。
			//	$(this).addClass("selectedDetailData");
				// DEL 2011.09.20 endo -end-

				// ADD 2011.09.20 endo -begin-
				// 以前選択した枠が無いならば
				if(beforeSelectCal == null){
					beforeSelectCal = $(this);
					// クリックした枠リストを選択状態にする。
					$(this).addClass("selectedDetailData");
				}else{
					beforeSelectCal.removeClass("selectedDetailData");
						// クリックした枠リストを選択状態にする。
					$(this).addClass("selectedDetailData");
					beforeSelectCal = $(this);
				}
				// ADD 2011.09.20 endo -end-

				// マウスカーソルを砂時計にする。
				document.body.style.cursor = 'wait';

				// RIS識別IDを取得する。
				var risID = $(this).find(".risID").html();

				// 一覧表示エリアを更新する。
				$("#OrderDetailList > table > tbody > tr").each(function() {
					// order一覧においてクリックしたorder以外を非選択状態にする。
					$(this).removeClass("selectedOrder");
				});
				$("#OrderDetailList > table > tbody > tr").each(function() {
					if($(this).find("span.orderDetailRisID").text() == risID){

						// ADD 2011.09.20 endo -begin-
						// 選択されているオーダー行を覚えておく
						beforeSelectOrder = $(this);
						// ADD 2011.09.20 endo -end-

						// order一覧においてクリックしたorderを選択状態にする。
						$(this).addClass("selectedOrder");
						return false;
					}
				});

				// 詳細表示エリアを更新する。
				showIraiDetail(risID);

				// マウスカーソルを元に戻す。
				document.body.style.cursor = 'auto';
			});
		});
		
		// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更
		// 枠リスト右クリックメニュー表示
		$(div).find("div.detailData").each(function() {
			// 枠リスト右クリック
			$(this).bind("contextmenu", function() {
				// DEL 2011.09.20 endo -begin-
				// 同セル内の全ての枠リストを非選択状態にする。
			//	$(this).parent().parent().find("div.detailData").each(function() {
			//		$(this).removeClass("selectedDetailData");
			//	});

				// クリックした枠リストを選択状態にする。
			//	$(this).addClass("selectedDetailData");
				// DEL 2011.09.20 endo -end-

				// ADD 2011.09.20 endo -begin-
				// 以前選択した枠が無いならば
				if(beforeSelectCal == null){
					beforeSelectCal = $(this);
					// クリックした枠リストを選択状態にする。
					$(this).addClass("selectedDetailData");
				}else{
					beforeSelectCal.removeClass("selectedDetailData");
						// クリックした枠リストを選択状態にする。
					$(this).addClass("selectedDetailData");
					beforeSelectCal = $(this);
				}
				// ADD 2011.09.20 endo -end-

				// マウスカーソルを砂時計にする。
				document.body.style.cursor = 'wait';

				// RIS識別IDを取得する。
				// 2016.12.20 Mod T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更
				//var risID = $(this).find(".risID").html();
				risID = $(this).find(".risID").html();
				// 2016.12.20 Mod T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更
				
	        	// 該当オーダの検査種別IDを取得
	        	kensatypeID = $(this).find(".kensatypeID").html();
	        	
	        	// 該当オーダの予約時刻(KENSA_STARTTIME)を取得
	        	kensaStartTime = $(this).find(".kensaStartTime").html();
	        	
	        	// 該当オーダのステータスを取得
	        	status = $(this).find(".kensastatus").html();
	        	
	        	// 該当オーダの検査日を取得
	        	kensaDateDlg = $(this).find(".kensaDate").html();
	        	
	        	// 画面ステータスとDBのステータスに変更がないかチェック
	        	chekStatus(risID);

	        	// 検査室名を取得
	        	kensaSituTitle = $(this).find(".kensaSituTitle").html();
	        	// 検査室IDを取得
	        	kensaSituID = $(this).find(".kensaSituID").html();
	        	
				// 一覧表示エリアを更新する。
				$("#OrderDetailList > table > tbody > tr").each(function() {
					// order一覧においてクリックしたorder以外を非選択状態にする。
					$(this).removeClass("selectedOrder");
				});
				$("#OrderDetailList > table > tbody > tr").each(function() {
					if($(this).find("span.orderDetailRisID").text() == risID){

						// ADD 2011.09.20 endo -begin-
						// 選択されているオーダー行を覚えておく
						beforeSelectOrder = $(this);
						// ADD 2011.09.20 endo -end-

						// order一覧においてクリックしたorderを選択状態にする。
						$(this).addClass("selectedOrder");
						return false;
					}
				});

				// 詳細表示エリアを更新する。
				showIraiDetail(risID);

				// マウスカーソルを元に戻す。
				document.body.style.cursor = 'auto';
			});
		});

		// 枠リスト右クリックメニュー表示
		$(div).find("div.detailData").each(function() {

			$(this).contextMenu("change",{
	        	bindings: {
	        		"timeRoomChange": function(t) {
	        			
	        			// 該当オーダの予定検査室取得
	        			changeExamRoomDlg(kensatypeID);
	        			
	        			// 取得した予約時刻を初期表示
	        			insertReservationTimeDlg(kensaStartTime);

	        			// ダイアログボックスを開く
						$('#timelaboratoryDialog').dialog('open');
						
	        		}
	        	},
	       	 	menuStyle: {
		       		width: '165px'
	        	}
			});
		});
		// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更
		
		// データの有無判定
		// ある場合は親要素TDにクラス hasdata を付与する。
		var dataItems = $(div).find("div.dataItem");
		if (dataItems.size() > 0) {
			$(div.parentNode).addClass("hasdata");
		}
	}
}

/*
 * 指定セルの詳細データを表示する。
 */
function showDetailData(td) {
	var existsDetailData = false;

	// 簡易データを非表示にする。
	$(td).find("div.simpleData").each(function() {
		$(this).hide();
	});

	// 詳細データを表示する。
	$(td).find("div.detailData").each(function() {
		$(this).show();
		existsDetailData = true;
	});

	// 詳細データがあるならカラム幅を広げる。
	if (existsDetailData) {
		var headerID = "#header" + $(td).find("span.date").html();
		var prevWidth = $(headerID).width();
		$(headerID).addClass("showDetail");
		var dataID = "#data" + $(td).find("span.date").html();
		$(dataID).addClass("showDetail");

		// showDetailクラス設定で幅が広くならない場合は、幅を狭めないようにする。
		var newWidth = $(headerID).width();
		if (newWidth < prevWidth) {
			$(headerID).css('width', 'auto');
			$(dataID).css('width', 'auto');
		}
	}
}

/*
 * 指定セルの簡易データを表示する。
 */
function showSimpleData(td) {
	// 簡易データを表示する。
	$(td).find("div.simpleData").each(function() {
		$(this).show();
	});
	// 詳細データを非表示にする。
	$(td).find("div.detailData").each(function() {
		$(this).hide();
	});

	// カラム幅を元に戻す。
	var headerID = "#header" + $(td).find("span.date").html();
	$(headerID).removeClass("showDetail");
	var dataID = "#data" + $(td).find("span.date").html();
	$(dataID).removeClass("showDetail");

	// 指定セル内の詳細データは未選択状態とする。
	$(td).find("div.detailData").each(function() {
		$(this).removeClass("selectedDetailData");
	});
}

/**
 * nodeの全子ノードを削除
 */
function removeAllChild(node){
	while(node.childNodes.length > 0){
		node.removeChild(node.childNodes[0]);
	}
	return node;
}

function selectedAllClear() {
	var dateblock = document.getElementById("dateblock");
	var timeblock = document.getElementById("timeblock");
	var	divNodes = null;
	var div = null;

	// dateblock
	divNodes = dateblock.getElementsByTagName("div");
	for (var i = 0; i< divNodes.length; i++) {
		divNodes = dateblock.getElementsByTagName("div");
		div = divNodes[i];
		if (!$(div).hasClass("date"))
			continue;

		// 選択状態のクリア
		if ($(div.parentNode).hasClass("selected")) {
			$(div.parentNode).removeClass("selected");
		}
	}
	// timeblock
	divNodes = timeblock.getElementsByTagName("div");
	for (var i = 0; i < divNodes.length; i++) {
		div = divNodes[i];
		if (!$(div).hasClass("data"))
			continue;

		// 選択状態のクリア
		if ($(div.parentNode).hasClass("selected")) {
			$(div.parentNode).removeClass("selected");
		}
		/*
		 * // data所有状態のクリア else if ($(div.parentNode).hasClass("hasdata")) {
		 * $(div.parentNode).removeClass("hasdata"); } // data所有状態再判定 if (
		 * div.childNodes.length > 3) { $(div.parentNode).addClass("hasdata"); }
		 */

		var inputNodes = div.getElementsByTagName("input");
		if (inputNodes.length == 1) {
			inputNodes[0].style.visibility = "hidden";
		}
	}

	if (selectedTd != null) {
		showSimpleData(selectedTd);
		selectedTd = null;
	}
}

/**
 * カレンダー上の新規登録ボタンクリック時の処理
 */
function addOrder() {
	// カレンダー選択イベントを発生しないようにするため、オーバーレイを表示する。
	changeOverlayStatus(true);
	changeSelectBoxStatus(false);

	showNewOrderForm(this.datetime);
}

function dialogClose(){
	changeStoreFormStatus(false);
}

function convertNumber(str) {
	return str - 0;
}

function getOrderKensaDate() {
	var yearElem = document.getElementById("orderKensaYear");
	var monthElem = document.getElementById("orderKensaMonth");
	var dayElem = document.getElementById("orderKensaDay");

	var year = yearElem.value;
	var month = monthElem.value;
	var day = dayElem.value;

	var ret = false;
	// NULLチェック
	ret = checkNull(year, month, day)
	if (!ret) {
		alert("検索日時を入力してください。");
		return -1;
	}
	// 2Byte文字チェック
	ret = check2ByteChar(year, month, day);
	if (!ret) {
		alert("検索日時に全角文字は入力できません。");
		return -1;
	}
	// 日付の妥当性チェック
	ret = checkDate(year, month, day);
	if (!ret) {
		alert("入力された日付が正しくありません。");
		return -1;
	}

// var dDate = new Date(year, month - 1, day);
// ret = compareToToday(dDate);
// if (!ret) {
// alert("本日以前には変更できません");
// return -1;
// }

	var nDate = year * 10000 + month * 100 + day*1;
	return nDate;
}

function compareToToday(date) {
   	var today = new Date();
   	var cToday = new Date(today.getFullYear(), today.getMonth(), today.getDate());
   	if ( date.getTime() < cToday.getTime() ) {
       	return false;
	}
	return true;
}

/*******************************************************************************
 * 全角/半角文字判定
 *
 * 引数 ： str チェックする文字列 flg 0:半角文字、1:全角文字 戻り値： true:含まれている、false:含まれていない
 *
 ******************************************************************************/
function checkLength(str,flg) {
	for (var i = 0; i < str.length; i++) {
		var c = str.charCodeAt(i);
		// Shift_JIS: 0x0 ～ 0x80, 0xa0 , 0xa1 ～ 0xdf , 0xfd ～ 0xff
		// Unicode : 0x0 ～ 0x80, 0xf8f0, 0xff61 ～ 0xff9f, 0xf8f1 ～ 0xf8f3
		if ( (c >= 0x0 && c < 0x81) || (c == 0xf8f0) || (c >= 0xff61 && c < 0xffa0) || (c >= 0xf8f1 && c < 0xf8f4)) {
			if(!flg)
				 return true;
		} else {
			if(flg)
				return true;
		}
	}
	return false;
}

function getOrderKensaStartTime() {
	var hourElem = document.getElementById("orderKensaStartHour");
	var minuteElem = document.getElementById("orderKensaStartMinute");

	var hour = hourElem.value;
	var minute = minuteElem.value;
	var time = hour*100 + minute*1;

	var configStartTime = $("#configStartTime").val();
	var configEndTime = $("#configEndTime").val();

	// message作成用
	var csTime = new String(configStartTime);
	while (csTime.length < 4) {
		csTime = "0" + csTime;
	}
	var csHour = csTime.substring(0,2);
	var csMinute = csTime.substring(2,4);

	var ceTime = new String(configEndTime);
	while (ceTime.length < 4) {
		ceTime = "0" + csTime;
	}
	var ceHour = ceTime.substring(0,2);
	var ceMinute = ceTime.substring(2,4);

	if (hour == null || hour.length == 0 || minute == null || minute.length == 0) {
		alert("検査時刻を入力してください。");
		return -1;
	}
	if (checkLength(hour, 1) || checkLength(minute, 1) ) {
		alert("検査時刻に全角文字は入力できません。");
		return -1;
	}
	if (hour > 23 || minute > 59) {
		alert("入力された検査時刻が正しくありません。");
		return -1;
	}
	if (time < configStartTime || configEndTime < time){
		alert("検査時刻は" + csHour + "時" + csMinute + "分から" + ceHour + "時" + ceMinute + "分までで入力してください。");
		return -1;
	}
	if(newOrder){
		var endTime = null;

		if (orderTimeFrame >= 60) {
			// 時間幅が60を超えている時は以下の計算で時間の計算となる。
			endTime = startTime + 40 + orderTimeFrame;
		} else {
			endTime = startTime + orderTimeFrame;
		}

		var strEndTime = new String(endTime);
		var endMinute = null;
		if(strEndTime.length ==3){
			endMinute = strEndTime.substring(1, 3);
		}else if(strEndTime.length ==4){
			endMinute = strEndTime.substring(2, 4);
		}

		if (endMinute >= 60) {
			// 下二桁が60を超えていたら40を足せば時間の計算と同じ値となる。
			endTime = endTime + 40;
		}
		if(endTime > settingCalenderEndTime){
			endTime = settingCalenderEndTime;
		}
		if(time < startTime || time >= endTime){
			var jougen = endTime -1;
			var strJougen = new String(jougen);
			var temp = strJougen.substring(strJougen.length-2, strJougen.length);
			if (temp >= 60) {
				// 下二桁が60を超えていたら40を足せば時間の計算と同じ値となる。
				jougen = jougen - 40;
			}
			var strStart = new String(startTime);
			var strJougen = new String(jougen);
			var starthour = null;
			var joukenhour = null;
			var startMinute = null;
			var joukenMinute = null;

			if(strStart.length == 3){
				 starthour = strStart.substring(0,1);
				 startMinute = strStart.substring(1, 3);
			}else if(strStart.length == 4){
				 starthour = strStart.substring(0,2);
				  startMinute = strStart.substring(2, 4);
			}else if(strStart.length == 1){
				starthour = "00";
				startMinute = "0" + strStart;
			}else if(strStart.length == 2){
				starthour = "00";
				startMinute = strStart;
			}
			if(strJougen.length == 3){
				joukenhour = strJougen.substring(0,1);
				joukenMinute = strJougen.substring(1, 3);
			}else if(strJougen.length == 4){
				joukenhour = strJougen.substring(0,2);
				joukenMinute = strJougen.substring(2, 4);
			}else if(strJougen.length == 1){
				joukenhour = "00";
				joukenMinute = "0" + strJougen;
			}else if(strJougen.length == 2){
				joukenhour = "00";
				joukenMinute = strJougen;
			}
			
			strStart = starthour + ":" + startMinute;
			strJougen = joukenhour + ":" + joukenMinute;
			alert("検査時刻は" + strStart + "～" + strJougen + "の間の時刻を入力してください。");
			return -1;
			}
		}
	return hour * 100 + minute * 1;
	}

function checkChangeToPastDay(day, time) {
	var defYear = $("#defYear").val();
	var defMonth = $("#defMonth").val();
	var defDay = $("#defDay").val();
	var defHour = $("#defHour").val();
	var defMinute = $("#defMinute").val();
	// 日付、時間のどちらかに変更があれば、本日以前に変更されていないかチェックを入れる

	if ( (day != parseInt(defYear, 10) * 10000 + parseInt(defMonth, 10) * 100 + parseInt(defDay, 10))
			|| (time != parseInt(defHour, 10) * 100 + parseInt(defMinute, 10)) ) {
		var sDate = new String(day);
		// dayが8桁より小さければ0Padding
		while (sDate.length < 8){
			sDate = "0" + sDate;
		}
		var dDate = new Date(sDate.substring(0,4), (sDate.substring(4,6) - 1), sDate.substring(6,8));
		ret = compareToToday(dDate);
		if (!ret) {
			alert("本日以前には変更できません");
			return false;
		}
	}
	return true;
}

/**
 * 今日の日付をセットする
 */
function setToday() {
//	document.body.style.cursor = 'wait';

	var date = new Date();

	// 現在表示している日時範囲を記述しなおす。
	writeCurrentSearchDate(date);

	// カレンダー更新
	changeCalendar(date);

//	document.body.style.cursor = 'auto';
}

/**
 * 現在画面表示されている日付を取得して、移動後の日付を返す
 */
function setOrderDay(param) {
//	document.body.style.cursor = 'wait';

	var addDays = 0;
	var addMonths = 0;
	var sDestDate;
	if (param == "nextWeek"){
		addDays = 7;
		sDestDate = getOrderDate(addDays);
	} else if (param == "nextDay") {
		addDays = 1;
		sDestDate = getOrderDate(addDays);
	} else if (param == "prevDay") {
		addDays = -1;
		sDestDate = getOrderDate(addDays);
	} else if (param == "prevWeek") {
		addDays = -7;
		sDestDate = getOrderDate(addDays);
	} else if (param == "nextMonth") {
		addMonths = 1;
		sDestDate = getOrderMonth(addMonths);
	} else if (param == "prevMonth") {
		addMonths = -1;
		sDestDate = getOrderMonth(addMonths);
	}

	// 現在表示している日時範囲を記述しなおす。
	writeCurrentSearchDate(sDestDate);

	// 西暦2桁以下が返ってきたときはエラー
	if (sDestDate == null) {
		alert("100/01/01 以前には変更できません。")

//		document.body.style.cursor = 'auto';
		return;
	}
	// カレンダー更新
	changeCalendar(sDestDate);

//	document.body.style.cursor = 'auto';
}

/**
 * 移動後の日付を返す(日単位のシフト)
 */
function getOrderDate(addDays) {
	var baseSec = currentCalendarDate.getTime();

	var	targetSec = baseSec + (addDays * 86400000);

	var targetDate = new Date();
	targetDate.setTime(targetSec);

	/*
	 * 本日より前なら本日にする。 var today = new Date(); if (targetDate < today) targetDate =
	 * today;
	 */

	// 西暦が3桁以下ならばnullを返す
	if (targetDate.getFullYear() < 100) {
		targetDate = null;
	}
	return targetDate;
}

/**
 * 移動後の日付を返す(月単位のシフト)
 */
function getOrderMonth(addMonths) {
	var year = currentCalendarDate.getFullYear();
	var month = currentCalendarDate.getMonth() + 1;
	var day = currentCalendarDate.getDate();

	month += addMonths;

	// 一つ前の月の末日を取得
	var date = new Date(year, month, 0);
	if (day > date.getDate())
		day = date.getDate();

	var targetDate = new Date(year, month - 1, day);

	// 西暦が3桁以下ならばnullを返す
	if (targetDate.getFullYear() < 100) {
		targetDate = null;
	}
	return targetDate;
}

/**
 * 入力された日付をセット
 */
function setInputDate() {
	// 日付の取得
	var date = getInputDate();
	if (date == -1)
		return false;

	return true;
}

/**
 * 検索条件部に入力された日付を取得する
 */
function getInputDate() {
	var searchDate = document.getElementById("searchDate").childNodes[0].nodeValue;
	// 2015.02.27 Mod S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
	var startDate = searchDate;
	//var startDate = searchDate.split("～")[0];
	// 2015.02.27 Mod S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更

	var year = startDate.split("/")[0];
	var month = startDate.split("/")[1];

	// ここだけは (曜日)部分も含まれてしまう
	var temp = startDate.split("/")[2];
	var day = temp.split("(")[0];

	var ret = false;
	// NULLチェック
	ret = checkNull(year, month, day)
	if (!ret) {
		alert("検索日時を入力してください。");
		return -1;
	}
	// 2Byte文字チェック
	ret = check2ByteChar(year, month, day);
	if (!ret) {
		alert("検索日時に全角文字は入力できません。");
		return -1;
	}
	// 日付の妥当性チェック
	ret = checkDate(year, month, day);
	if (!ret) {
		alert("入力された日付が正しくありません。");
		return -1;
	}
	// Dateに変換
	var date = new Date(year, month - 1, day);

	return date;
}

function checkNull(year, month, day){
	if(year == null || year.length == 0 || month == null || month.length == 0 || day == null || day.length == 0){
		return false;
	}
	return true;
}

function check2ByteChar(year, month, day){
	if (checkLength(year, 1) || checkLength(month, 1) || checkLength(day, 1)) {
		return false;
	}
	return true;
}

function checkDate(year, month, day){
	var date = new Date(year, month - 1, day);
    if(date == null || date.getFullYear() != year || date.getMonth() + 1 != month || date.getDate() != day) {
        return false;
    }
    return true;
}


function getKensaType() {
	return searchKensaType;
}

function getExamRoom() {
	return searchExamRoom;
}

function getTimeFrame() {
	return searchTimeFrame;
}

function getRIOrder() {
	return searchRIOrder;
}

function getKensaStatus() {
	return searchKensaStatus;
}

function getSection() {
	return searchSection;
}

function getByoutou() {
	return searchByoutou;
}

function getKanjaNyugai() {
	return searchKanjaNyugai;
}

function getDenpyouNyugai() {
	return searchDenpyouNyugai;
}

// 2012.01.12 Add Yk.Suzuki@CIJ Start 表示切替
function getDisplayType() {
	return searchDisplayType;
}
// 2012.01.12 Add Yk.Suzuki@CIJ End   表示切替

function setAutoSelectDateTime(datetime) {
	if (datetime == null)
		datetime = "";

	autoSelectDateTime = datetime;
}

function changeCalendar(date) {
	var kensaType = getKensaType();
	var examRoom = getExamRoom();
	var riOrder = getRIOrder();
	var kensaStatus = getKensaStatus();
	var section = getSection();
	var byoutou = getByoutou();
	var kanjaNyugai = getKanjaNyugai();
	var denpyouNyugai = getDenpyouNyugai();

	var timeFrame = getTimeFrame();

	var year = date.getFullYear();
	var month = date.getMonth() + 1;
	var day = date.getDate();
	if (month < 10) {
		month = "0" + month;
	}
	if (day < 10) {
		day = "0" + day;
	}
	var datetime = year + "" + month + "" + day;

	// 2016.12.20 Mod T.onoda@Cosmo Start KUMA205-3-CAL003 検査室毎画面　縦表示
	//var url = "ReservationServlet?action=calendar"
	//	+ "&date=" + datetime
	//	+ "&timeFrame=" + timeFrame
	//	+ "&kensaType=" + kensaType;
	var url = null;
	var pair=location.search.substring(1);

	if (pair == "action=kensasitu_v") {
		url = "ReservationServlet?action=kensasitu_v_show"
			+ "&date=" + datetime
			+ "&timeFrame=" + timeFrame
			+ "&kensaType=" + kensaType;
	} else if (pair == "action=kensasitu_h") {
		url = "ReservationServlet?action=kensasitu_h_show"
			+ "&date=" + datetime
			+ "&timeFrame=" + timeFrame
			+ "&kensaType=" + kensaType;
	} else if (pair == "action=calendar") {
		url = "ReservationServlet?action=calendar_show"
			+ "&date=" + datetime
			+ "&timeFrame=" + timeFrame
			+ "&kensaType=" + kensaType;
	}
	// 2016.12.20 Mod T.onoda@Cosmo End KUMA205-3-CAL003 検査室毎画面　縦表示

	if (examRoom != null && examRoom.length > 0)
		url += "&examRoom=" + examRoom;
	if (riOrder != null && riOrder.length > 0)
		url += "&riOrder=" + riOrder;
	if (kensaStatus != null && kensaStatus.length > 0)
		url += "&kensaStatus=" + kensaStatus;
	if (section != null && section.length > 0)
		url += "&section=" + section;
	if (byoutou != null && byoutou.length > 0)
		url += "&byoutou=" + byoutou;
	if (kanjaNyugai != null & kanjaNyugai.length > 0)
		url += "&kanjaNyugai=" + kanjaNyugai;
	if (denpyouNyugai != null && denpyouNyugai.length > 0)
		url += "&denpyouNyugai=" + denpyouNyugai;
	url += addPinParameter();

	loadCalendar(url);
}

function loadCalendar(url) {
	logger.trace(url);
	document.body.style.cursor = 'wait';
	blockSearch();

	var timeblock = document.getElementById("timeblock");
	var currentVScrollPosition = 0;
	if (timeblock) {
		currentVScrollPosition = timeblock.scrollTop;
	}

	$("#CalendarBlock").load(url, null, function(responseText, status, XMLHttpRequest) {
		resizeCalendar();
		initCalendarItem();

		if (autoSelectDateTime != null && autoSelectDateTime.length > 0) {
			autoSelectCalendar(currentVScrollPosition);
			setAutoSelectDateTime(null);
		}

		document.body.style.cursor = 'auto';
		unblockSearch();
	});
}

function blockSearch() {
	document.body.style.cursor = 'wait';
	$("#header input:button").attr('disabled', true);
	$("#header button").attr('disabled', true);
	$("#header select").attr('disabled', true);
	$("#searchDate").attr('disabled', true);
	// 2015.02.27 Del S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更 
	//$("#searchDate2").attr('disabled', true);
	// 2015.02.27 Del S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更 
}

function unblockSearch() {
	document.body.style.cursor = 'auto';
	$("#header input:button").attr('disabled', false);
	$("#header button").attr('disabled', false);
	$("#header select").attr('disabled', false);
	$("#searchDate").attr('disabled', false);
	// 2015.02.27 Del S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
	//$("#searchDate2").attr('disabled', false);
	// 2015.02.27 Del S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更
}

function autoSelectCalendar(currentVScrollPosition) {
	var timeblock = document.getElementById("timeblock");

	if (orderDetailMode == 0) {
		// 日付が選択選択されている場合
		var dateblock = document.getElementById("dateblock");
		if (dateblock == null)
			return;
		var divNodes = dateblock.getElementsByTagName("div");
		var autoSelectDate = autoSelectDateTime.substring(0, 8);
		for (var i = 0; i < divNodes.length; i++) {
			var div = divNodes[i];
			if(!$(div).hasClass("date"))
				continue;

			var strDateTime = $(div).find("span.datetime").text();
			var strDate = strDateTime.substring(0, 8);

			if (strDate == autoSelectDate) {
				var parentTD = div.parentNode;
				parentTD.click();
				break;
			}
		}
	} else {
		// 時間枠が選択されている場合
		if (timeblock == null)
			return;

		var divNodes = timeblock.getElementsByTagName("div");
		for (var i = 0; i < divNodes.length; i++) {
			var div = divNodes[i];
			if (!$(div).hasClass("data"))
				continue;

			if ($(div).find("span.datetime").text() == autoSelectDateTime) {
				var parentTD = div.parentNode;
				parentTD.click();
				break;
			}
		}
	}
	if (currentVScrollPosition > 0)
		timeblock.scrollTop = currentVScrollPosition;
}

function showIraiDetail(risID){
	var url = "about:blank";
	if (risID != null && risID.length > 0) {
		if(risID.indexOf(":") != -1){
			risID = risID.split(":")[0];
		}
		url = htmlPath+"&RIS_ID="+risID;
		logger.trace(url);
	}

	var iframe = $("#IraiSyosaiBlock > iframe");
	iframe.attr("src",url);
}

function showOrderDetail(risIDParam) {
	var url = "ReservationServlet?action=orderDetail"
		+ "&risID=" + risIDParam
		+ addPinParameter();

	loadOrderDetail(url, risIDParam);
}

function loadIraiDetail(url) {
	logger.trace(url);
	document.body.style.cursor = 'wait';
	$("#IraiSyosaiBlock").load(url, null, function(responseText, status, XMLHttpRequest) {
		document.body.style.cursor = 'auto';
	});
}

function loadOrderDetail(url, risIDParam) {

	// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL003 検査室毎画面　縦表示
	var pair=location.search.substring(1);
	if (pair == "action=kensasitu_v" || pair == "action=kensasitu_h") {
		$("#timeblock").css("height", 798);
		$("#main").css("bottom", 0);
		$("#OrderArea").css("height", 0);
		//return false;
	}
	// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL003 検査室毎画面　縦表示

	logger.trace(url);
	document.body.style.cursor = 'wait';
	$("#OrderBlock").load(url, null, function(responseText, status, XMLHttpRequest) {
		// 一覧表示エリアに表示する。
		resizeOrderDetail();

		// クリック時に選択する
		// (Windows Server 2008 と R2 で IE8 で <tr> の onclick だと選択されない)
		$("#OrderDetailList tr").each(function() {
			$(this).bind("click", function() {
				selectOrder(this)
			});
		});

		var temp = risIDParam.split(":");
		if (temp.length == 1 && risIDParam.length != 0){
			// 最初の行を選択状態にする。
			selectFirstRowOrderDetail();
		}

		document.body.style.cursor = 'auto';
	});

	// 2015.03.24 Add S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
	document.body.style.cursor = 'auto';
	// 2015.03.24 Add S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更
}

/*
 * * Paramのdateが今日以前の日付であればtrueを返す
 */
function compareDate(date) {
	var ret = false;

	var dDate = new Date(date.substring(0,4), date.substring(4,6) - 1 , date.substring(6,8));

	var today = new Date();
	var dToday = new Date(today.getYear(), today.getMonth() , today.getDate());

	if (dDate < dToday)
		ret = true;

	return ret;
}

function searchOrder() {
	var date = getInputDate();
	if (date == -1)
		return;

	// 検索条件
	searchKensaType = $("#kensaTypeID").val();
	searchExamRoom = $("#kensaRoomID").val();
	searchExamRoomName = $("#kensaRoomText").val();
	searchKensaStatus = $("#kensaStatusID").val();
	searchRIOrder = $("#riOrderID").val();
	searchTimeFrame = $("#timeFrame").val();
	searchSection = $("#sectionID").val();
	searchByoutou = $("#byoutouID").val();
	searchKanjaNyugai = $("#kanjaNyugaiID").val();
	searchDenpyouNyugai = $("#denpyouNyugaiID").val();
	searchDisplayType = $("#displayType").val(); // 2012.01.12 Add Yk.Suzuki@CIJ 表示切替

	// cookieに記憶(有効期間10年)
	$.cookie(COOKIE_KENSA_TYPE, searchKensaType, { expires: COOKIE_EXPIRES });
	$.cookie(COOKIE_EXAM_ROOM,  searchExamRoom,  { expires: COOKIE_EXPIRES });
	$.cookie(COOKIE_EXAM_ROOMNAME, searchExamRoomName, { expires: COOKIE_EXPIRES});
	$.cookie(COOKIE_TIME_FRAME, searchTimeFrame, { expires: COOKIE_EXPIRES });
	$.cookie(COOKIE_KENSA_STATUS, searchKensaStatus, { expires: COOKIE_EXPIRES });
	$.cookie(COOKIE_RI_ORDER,  searchRIOrder,  { expires: COOKIE_EXPIRES });
	$.cookie(COOKIE_SECTION, searchSection, { expires: COOKIE_EXPIRES });
	$.cookie(COOKIE_BYOUTOU, searchByoutou, { expires: COOKIE_EXPIRES });
	$.cookie(COOKIE_KANJA_NYUGAI, searchKanjaNyugai, { expires: COOKIE_EXPIRES });
	$.cookie(COOKIE_DENPYOU_NYUGAI, searchDenpyouNyugai, { expires: COOKIE_EXPIRES });
	$.cookie(COOKIE_DISPLAY_TYPE, searchDisplayType, { expires: COOKIE_EXPIRES }); // 2012.01.12 Add Yk.Suzuki@CIJ 表示切替

	currentCalendarDate = date;
	changeCalendar(currentCalendarDate);

	showIraiDetail("");
	showOrderDetail("");
}

function displayTypeChange() {
	var type = $("#displayType").val();
	if (type == "kanja") {
		$("span.startTime").each(function() {
			$(this).show();
		});
		$("span.kanjiSimei").each(function() {
			$(this).show();
		});
		$("span.buiSet").each(function() {
			$(this).hide();
		});
	} else {
		$("span.startTime").each(function() {
			// 2012.03.06 Mod Yk.Suzuki@CIJ Start SSGH-4-003
			$(this).show();
			//$(this).hide();
			// 2012.03.06 Mod Yk.Suzuki@CIJ End   SSGH-4-003
		});
		$("span.kanjiSimei").each(function() {
			$(this).hide();
		});
		$("span.buiSet").each(function() {
			$(this).show();
		});
	}
	// 検索条件保存
	searchDisplayType = type;
}

function enableSearchButton() {
	$("searchBtn").removeAttr("disabled");
}

function disableSearchButton() {
	$("searchBtn").attr("disabled", "disabled");
}

function getKanjaID(){
	return $("#orderKanjaID").val();
}

function examRoomChanged(examRoom) {
	changeKensaKiki(examRoom);
}

function changeKensaKiki(examRoom) {
	var selectElement = document.getElementById("orderKensaKiki");
	if (selectElement == null)
		return;

	selectElement.options.length = 0;

	if (examRoom == null || examRoom.length == 0) {
		elem = document.createElement("option");
		elem.value = "";
		elem.text = "";
		selectElement.options[selectElement.options.length] = elem;

		return;
	}

	var url = "ReservationServlet?action=kensaKiki"
		+ "&examRoom=" + examRoom + addPinParameter();

	logger.trace(url);
//	document.body.style.cursor = 'wait';
	$.getJSON(url, function(json) {
		var kensaKikis = json;
		$.each(kensaKikis, function(i, kensaKiki) {
			elem = document.createElement("option");
			elem.value = kensaKiki.id;
			elem.text = kensaKiki.name;
			selectElement.options[selectElement.options.length] = elem;
		});
//		document.body.style.cursor = 'auto';
	});
}

function changePattern(kensaType) {
	var selectElement = document.getElementById("orderPattern");
	if (selectElement == null)
		return;

	selectElement.options.length = 0;

	var url = "ReservationServlet?action=patternOrder"
		+ "&kensaType=" + kensaType + addPinParameter();

	logger.trace(url);
//	document.body.style.cursor = 'wait';
	$.getJSON(url, function(json) {
		var patternOrders = json;
		$.each(patternOrders, function(i, patternOrder) {
			elem = document.createElement("option");
			elem.value = patternOrder.code;
			elem.text = patternOrder.name;
			selectElement.options[selectElement.options.length] = elem;
		});
//		document.body.style.cursor = 'auto';
	});
}

// 未使用
function patternChanged(patternCode) {
	changePatternKindLabel(patternCode);
	if (patternCode.length == 0) {
	}
}

function sectionChanged(section) {
	changeDoctor(section);
}

function setWindowBeforeUnload(func) {
	if (func != null)
		$(window).bind("beforeunload", func);
	else
		$(window).unbind("beforeunload");
}

function addPinParameter() {
	var date = new Date();
	return "&pin=" + date.getTime();
}

// 検索条件記述エリアの現在の表示範囲の日時を記述するまう。
function writeCurrentSearchDate(startDate){
	var startYear = startDate.getFullYear();
	var startManth =  new String(startDate.getMonth() + 1);
	var startdate =  new String(startDate.getDate());
	var startYoubi = weekchars[ startDate.getDay() ];

	if(startManth.length == 1){
		startManth = "0" + startManth;
	}

	if(startdate.length == 1){
		startdate = "0" + startdate;
	}

	var baseSec = startDate.getTime();

	currentCalendarDate = startDate;
	
	// 2015.02.27 Del S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
	// 終了日時取得処理
    //var endDt = getOrderDate(6);

	//var endYear = endDt.getFullYear();
	//var endManth = new String(endDt.getMonth() + 1);
	//var enddate =  new String(endDt.getDate());
	//var endYoubi = weekchars[ endDt.getDay() ];

	//if(endManth.length == 1){
	//	endManth = "0" + endManth;
	//}

	//if(enddate.length == 1){
	//	enddate = "0" + enddate;
	//}
	// 2015.02.27 Del S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更

	//2011.Dec カレンダー追加
	document.getElementById("searchDate").childNodes[0].nodeValue = startYear + "/" + startManth + "/" + startdate +"(" + startYoubi +")";
	// 2015.02.27 Del S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
	//document.getElementById("searchDate2").childNodes[0].nodeValue = endYear + "/" + endManth + "/" + enddate +"(" + endYoubi +")" ;
	// 2015.02.27 Del S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更
}

function showCalendar() {
	var date = getInputDate();
	var pos = $("#searchDate").offset();
	$("#searchDate").datepicker("dialog", date,
		function(dateText) {
			var searchDate = dateText;
			var year = searchDate.split("/")[0];
			var month = searchDate.split("/")[1];
			var day = searchDate.split("/")[2];
			var startDt = new Date(year, month - 1, day);

			writeCurrentSearchDate(startDt);

			searchOrder();
		},
		{
			showButtonPanel: true,
			showAnim: 'slideDown',
			changeYear: true,
			changeMonth: true
		},
		[pos.left, pos.top + $("#searchDate").height()]
	);
}

function changeOKButtonState(checkBox) {
	var okButton = getDialogButton(".kensaTypeDialog", "OK");
	if (okButton == null)
		return;

	if(checkBox.checked){
		okButton.removeAttr("disabled");
	} else {
		var elements = $('#kensaTypeCheckBoxes table input:checkbox');
		var disableOKButton = true;
		elements.each(function(i, elem){
			if(elem.checked) {
				disableOKButton = false;
				return false;
			}
		});
		if (disableOKButton)
			okButton.attr("disabled", "disabled");
		else
			okButton.removeAttr("disabled");
	}
}

function changeAllButton(checkBox, id) {
	if (!checkBox.checked) {
		$("#" + id).attr('checked', false);
	}
}


// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更

// 予定時刻のセット
function insertReservationTimeDlg(kensaStartTime) {
	
	// 予約時刻コンボボックス初期化
	sl = document.getElementById('cmbReservationTimeDlg');
	while(sl.lastChild)
	{
		sl.removeChild(sl.lastChild)
	
	}
	
	// 引数の予約時刻が空白以外は該当オーダの予定時刻をセット
	if (kensaStartTime != null) {
		var startTime = kensaStartTime;
		var time = kensaStartTime.split(":");
		if(time[0].length == 1) {
			startTime = "0" + time[0] + ":" +  time[1];
		}
		$("#cmbReservationTimeDlg").prepend($("<option>").val(startTime).text(startTime));
	}
	
	// 配列の予約時刻をセット
	for ( var i in selectBox ) {
		$('#cmbReservationTimeDlg').append('<option value="' + selectBox[i] + '">' + selectBox[i] + '</option>');
	}

}

// 予定検査室のセット
function insertKensaRoomDlg(kensaRoomArray) {
	
	if(kensaRoomArray.length == 0) {
		$('#cmbPlansLaboratoryDlg').attr("disabled",true);
		return;
	}
	
	// 予定検査室コンボボックス初期化
	sl = document.getElementById('cmbPlansLaboratoryDlg');
	while(sl.lastChild)
	{
		sl.removeChild(sl.lastChild)
	
	}
	// 取得した検査室配列分回す
	for(i=0;i<kensaRoomArray.length;i++){
		$("#cmbPlansLaboratoryDlg").append($("<option>").val(kensaRoomArray[i].id).text(kensaRoomArray[i].name));
	}

	$("#cmbPlansLaboratoryDlg").val(kensaSituID);

}
// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更

function insertKensaType() {
	var form = document.getElementById('kensaTypeCheckBoxes');
	var html = "";
	var j = 0;
	html += "<table>"
	for(i=0;i<kensaTypeArray.length;i++){
		if(j == 0) html += "<tr>";
		html += "<td>";
		html += "<input type=\"checkbox\" onclick='changeAllButton(this, \"selectAllKensaType\"); changeOKButtonState(this)' id=\"checkboxKensaType"+i+"\" class=\"kensaType\" name=\"" + kensaTypeArray[i].name + "\" value = \"" + kensaTypeArray[i].id + "\" ><label for=\"checkboxKensaType"+ i + "\">"+ kensaTypeArray[i].name + "</label></input>";
		html += "</td>";
		if(j++ == 2) {
			html += "</tr>";
			j = 0;
		}
	}

	var selectAll = "$('#kensaTypeCheckBoxes table input:checkbox').attr('checked', this.checked); var button = getDialogButton('.kensaTypeDialog', 'OK'); if (button) button.attr('disabled',!this.checked)";
	html += "</table><input type=\"checkbox\" id=\"selectAllKensaType\" onclick=\"" + selectAll + "\"><label for=\"selectAllKensaType\">全選択/全解除</label></input>";
	form.innerHTML = html;
}

function insertKensaRoom(kensaRoomArray) {
	var form = document.getElementById('kensaRoomCheckBoxes');
	var html ="";
	var j=0;
	if(kensaRoomArray.length == 0) {
		$('#openKensaRoomDialog').attr("disabled",true);
		return;
	}
	html += "<table>"
	for(i=0;i<kensaRoomArray.length;i++){
		if(j==0) html += "<tr>";
		html += "<td>";
		html += "<input type=\"checkbox\" onclick='changeAllButton(this, \"selectAllKensaRoom\")' id=\"checkboxKensaRoom"+i+"\" class=\"kensaRoom\" name=\"" + kensaRoomArray[i].name + "\" value = \"" + kensaRoomArray[i].id + "\" ><label for=\"checkboxKensaRoom"+ i + "\">"+ kensaRoomArray[i].name + "</label></input>";
		html += "</td>";
		if(j++==2){
			html+="</tr>";
			j=0;
		}
	}
	var selectAll = "$('#kensaRoomCheckBoxes table input:checkbox').attr('checked', this.checked);";
	html += "</table><input type=\"checkbox\" id=\"selectAllKensaRoom\" onclick=\"" + selectAll + "\"><label for=\"selectAllKensaRoom\">全選択/全解除</label></input>";
	form.innerHTML = html;
}

function insertRiOrder() {
	var form = document.getElementById('riOrderCheckBoxes');
	var html ="<ul style=\"list-style:none;\">";
	for(i=0;i<riOrderArray.length;i++){
		html += "<li><input type=\"checkbox\" onclick='changeAllButton(this, \"selectAllRiOrder\")' id=\"checkboxRiOrder"+i+"\" class=\"riOrder\" name=\"" + riOrderArray[i].name + "\" value = \"" + riOrderArray[i].id + "\" ><label for=\"checkboxRiOrder"+ i + "\">"+ riOrderArray[i].name + "</label></input></li>";
	}
	html += "</ul>"
	var selectAll = "$('#riOrderCheckBoxes input:checkbox').attr('checked', this.checked);";
	html += "<input type=\"checkbox\" id=\"selectAllRiOrder\" onclick=\"" + selectAll + "\"><label for=\"selectAllRiOrder\">全選択/全解除</label></input>";
	form.innerHTML = html;
}

function insertKensaStatus() {
	var form = document.getElementById('kensaStatusCheckBoxes');
	var html = "";
	var j = 0;
	html+="<table>"
	for(i=0;i<kensaStatusArray.length;i++){
		if(j == 0) html += "<tr>";
		html += "<td>";
		html += "<input type=\"checkbox\" onclick='changeAllButton(this, \"selectAllKensaStatus\")' id=\"checkboxKensaStatus"+i+"\" class=\"kensaStatus\" name=\"" + kensaStatusArray[i].name + "\" value = \"" + kensaStatusArray[i].id + "\" ><label for=\"checkboxKensaStatus"+ i + "\">"+ kensaStatusArray[i].name + "</label></input>";
		html += "</td>";
		if(j++ == 2) {
			html += "</tr>";
			j = 0;
		}
	}

	var selectAll = "$('#kensaStatusCheckBoxes table input:checkbox').attr('checked', this.checked);";
	html += "</table><input type=\"checkbox\" id=\"selectAllKensaStatus\" onclick=\"" + selectAll + "\"><label for=\"selectAllKensaStatus\">全選択/全解除</label></input>";
	form.innerHTML = html;
}

function insertSection() {
	var form = document.getElementById('sectionCheckBoxes');
	var html = "";
	var j = 0;
	html+="<table>"
	for(i=0;i<sectionArray.length;i++){
		if(j == 0) html += "<tr>";
		html += "<td>";
		html += "<input type=\"checkbox\" onclick='changeAllButton(this, \"selectAllSection\")' id=\"checkboxSection"+i+"\" class=\"section\" name=\"" + sectionArray[i].name + "\" value = \"" + sectionArray[i].id + "\" ><label for=\"checkboxSection"+ i + "\">"+ sectionArray[i].name + "</label></input>";
		html += "</td>";
		if(j++ == 2) {
			html += "</tr>";
			j = 0;
		}
	}

	var selectAll = "$('#sectionCheckBoxes table input:checkbox').attr('checked', this.checked);";
	html += "</table><input type=\"checkbox\" id=\"selectAllSection\" onclick=\"" + selectAll + "\"><label for=\"selectAllSection\">全選択/全解除</label></input>";
	form.innerHTML = html;
}

function insertByoutou() {
	var form = document.getElementById('byoutouCheckBoxes');
	var html = "";
	var j = 0;
	html+="<table>"
	for(i=0;i<byoutouArray.length;i++){
		if(j == 0) html += "<tr>";
		html += "<td>";
		html += "<input type=\"checkbox\" onclick='changeAllButton(this, \"selectAllByoutou\")' id=\"checkboxByoutou"+i+"\" class=\"byoutou\" name=\"" + byoutouArray[i].name + "\" value = \"" + byoutouArray[i].id + "\" ><label for=\"checkboxByoutou"+ i + "\">"+ byoutouArray[i].name + "</label></input>";
		html += "</td>";
		if(j++ == 2) {
			html += "</tr>";
			j = 0;
		}
	}

	var selectAll = "$('#byoutouCheckBoxes table input:checkbox').attr('checked', this.checked);";
	html += "</table><input type=\"checkbox\" id=\"selectAllByoutou\" onclick=\"" + selectAll + "\"><label for=\"selectAllByoutou\">全選択/全解除</label></input>";
	form.innerHTML = html;
}

function insertKanjaNyugai() {
	var form = document.getElementById('kanjaNyugaiCheckBoxes');
	var html = "";
	var j = 0;
	html+="<table>"
	for(i=0;i<kanjaNyugaiArray.length;i++){
		if(j == 0) html += "<tr>";
		html += "<td>";
		html += "<input type=\"checkbox\" onclick='changeAllButton(this, \"selectAllKanjaNyugai\")' id=\"checkboxKanjaNyugai"+i+"\" class=\"kanjaNyugai\" name=\"" + kanjaNyugaiArray[i].name + "\" value = \"" + kanjaNyugaiArray[i].id + "\" ><label for=\"checkboxKanjaNyugai"+ i + "\">"+ kanjaNyugaiArray[i].name + "</label></input>";
		html += "</td>";
		if(j++ == 2) {
			html += "</tr>";
			j = 0;
		}
	}

	var selectAll = "$('#kanjaNyugaiCheckBoxes table input:checkbox').attr('checked', this.checked);";
	html += "</table><input type=\"checkbox\" id=\"selectAllKanjaNyugai\" onclick=\"" + selectAll + "\"><label for=\"selectAllKanjaNyugai\">全選択/全解除</label></input>";
	form.innerHTML = html;
}

function insertDenpyouNyugai() {
	var form = document.getElementById('denpyouNyugaiCheckBoxes');
	var html = "";
	var j = 0;
	html+="<table>"
	for(i=0;i<denpyouNyugaiArray.length;i++){
		if(j == 0) html += "<tr>";
		html += "<td>";
		html += "<input type=\"checkbox\" onclick='changeAllButton(this, \"selectAllDenpyouNyugai\")' id=\"checkboxDenpyouNyugai"+i+"\" class=\"denpyouNyugai\" name=\"" + denpyouNyugaiArray[i].name + "\" value = \"" + denpyouNyugaiArray[i].id + "\" ><label for=\"checkboxDenpyouNyugai"+ i + "\">"+ denpyouNyugaiArray[i].name + "</label></input>";
		html += "</td>";
		if(j++ == 2) {
			html += "</tr>";
			j = 0;
		}
	}

	var selectAll = "$('#denpyouNyugaiCheckBoxes table input:checkbox').attr('checked', this.checked);";
	html += "</table><input type=\"checkbox\" id=\"selectAllDenpyouNyugai\" onclick=\"" + selectAll + "\"><label for=\"selectAllDenpyouNyugai\">全選択/全解除</label></input>";
	form.innerHTML = html;
}

function closeKensaTypeDialog() {
	$('#kensaTypeDialog').dialog('close');
}

function selectOrder(tr) {
	var risID = $(tr).find("span.orderDetailRisID").text();

	if(risID == null || risID == ""){
		return;
	}

	// ADD 2011.09.20 endo -begin-
	// 以前選択したオーダーが無いならば
	if(beforeSelectOrder == null ){
		// 選択オーダーを覚えとく
		beforeSelectOrder = $(tr);
		// 選択状態にする
		$(tr).addClass("selectedOrder");
	}else{
		// 以前選択したオーダーは非選択状態にする。
		beforeSelectOrder.removeClass("selectedOrder");
		beforeSelectOrder = $(tr);
		// 選択状態にする
		$(tr).addClass("selectedOrder");
	}
	// ADD 2011.09.20 endo -end-

	// DEL 2011.09.20 endo -begin-
	// クリックしたorder以外を非選択状態にする。
//	$(tr).removeClass("selectedOrder");

//	$("tr").find("td.orderDetailLine").each(function() {
//		$(this).removeClass("selectedOrder");
//	});

	// クリックしたorderを選択状態にする。
//	$(tr).find("td.orderDetailLine").each(function() {
//		$(this).addClass("selectedOrder");
//	});
	// DEL 2011.09.20 endo -end-

	// ADD 2011.09.20 endo -start-
	// 枠リストの選択
	// 同セル内の全ての枠リストを非選択状態にする。
	$("#timeblock > table > tbody > tr > td").find("div.selectedDetailData").removeClass("selectedDetailData");

	// 枠リストの選択
	$("#timeblock > table > tbody > tr > td").find("div.detailData").each(function() {
		var calRisID = $(this).find(".risID").html();
		if(calRisID == risID){
		// クリックした枠リストを選択状態にする。
			$(this).addClass("selectedDetailData");
			return false;
		}
	});
	// ADD 2011.09.20 endo -end-

	// DEL 2011.09.20 endo -begin-
//	$("div").find("div.detailData").each(function() {
		// 同セル内の全ての枠リストを非選択状態にする。
//		$(this).each(function() {
//			$(this).removeClass("selectedDetailData");
//		});
//		var calRisID = $(this).find(".risID").html();
//		if(calRisID == risID){
		// クリックした枠リストを選択状態にする。
//			$(this).addClass("selectedDetailData");
//			return false;
//		}
//	});
	// DEL 2011.09.20 endo -end-

	showIraiDetail(risID);
}

/**
 * COOKIEの内容を元に、グローバル変数・ダイアログ表示用変数の設定を行う。
 *
 * 2014.05.07 追加
 * COOKIEの内容が、最新のDB情報と一致していない可能性がある為、
 * 最新DBの内容と一致していない項目は、グローバル変数の設定対象外にする。
 * なおダイアログ表示用変数は、不一致項目を省く処理が追加済。
 */
function setValuesFromCookie() {
	var str ="";
	var newIdList = ""; // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
	var kensaDataIDs = new Array();
	var roomDataIDs = new Array();
	var roomDataNames = new Array();
	var riOrderDataIDs = new Array();
	var kensaStatusDataIDs = new Array();
	var sectionIDs = new Array();
	var byoutouIDs = new Array();
	var kanjaNyugaiIDs = new Array();
	var denpyouNyugaiIDs = new Array();

	// 検査種別
	if($.cookie(COOKIE_KENSA_TYPE)){
		$('#kensaTypeID').val($.cookie(COOKIE_KENSA_TYPE));
		kensaDataIDs = $('#kensaTypeID').val().split(":");
		for(i=0;i<kensaTypeArray.length;i++){
			for(j=0;j<kensaDataIDs.length;j++){
				if(kensaTypeArray[i].id == kensaDataIDs[j]){
					kensaData.push({"name":kensaTypeArray[i].name, "id":kensaTypeArray[i].id});
					str+= kensaTypeArray[i].name + ", ";
					newIdList +=  kensaTypeArray[i].id +":"; // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
				}
			}
		}
		$('#kensaTypeText').val(str.substr(0,str.length-2));
		$('#kensaTypeID').val(newIdList.substr(0, newIdList.length-1)); // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
	}

	// 検査室
	str = "";
	newIdList = ""; // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
	changeExamRoom($('#kensaTypeID').val());
	if($('#kensaTypeID').val() != ""){
		$('#openKensaRoomDialog').attr("disabled",false);
	} else {
		$('#openKensaRoomDialog').attr("disabled",true);
		$('#kensaRoomText').val("");
		$('#kensaRoomID').val("");
	}
	if($.cookie(COOKIE_EXAM_ROOM) && $.cookie(COOKIE_EXAM_ROOMNAME)){
		$('#kensaRoomID').val($.cookie(COOKIE_EXAM_ROOM));
		$('#kensaRoomText').val($.cookie(COOKIE_EXAM_ROOMNAME));
		roomDataIDs = $('#kensaRoomID').val().split(":");
		roomDataNames = $('#kensaRoomText').val().split(", ");
		for(i=0;i<roomDataIDs.length;i++){
			// 2014.05.07 Mod T.Koudate@COSMO Start #2682 Cookie不正
			for(j=0; j<kensaRoomArrayGlobal.length; j++) {
				if (kensaRoomArrayGlobal[j].id == roomDataIDs[i]) {
					// DBに一致するものがある時だけ追加
					roomData.push({"name":roomDataNames[i], "id":roomDataIDs[i]});
					str       += kensaRoomArrayGlobal[j].name +", ";
					newIdList += kensaRoomArrayGlobal[j].id +":";
				}
			}
			//roomData.push({"name":roomDataNames[i], "id":roomDataIDs[i]});
			// 2014.05.07 Mod T.Koudate@COSMO End   #2682 Cookie不正
		}
		// 2014.05.07 Add T.Koudate@COSMO Start #2682 Cookie不正
		$('#kensaRoomID').val(newIdList.substr(0, newIdList.length-1));
		$('#kensaRoomText').val(str.substr(0,str.length-2));
		// 2014.05.07 Add T.Koudate@COSMO End   #2682 Cookie不正
	}

	// 枠
	if($.cookie(COOKIE_TIME_FRAME)){
		$('#timeFrame').val($.cookie(COOKIE_TIME_FRAME));
	}

	// RI区分
	str = "";
	newIdList = ""; // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
	if($.cookie(COOKIE_RI_ORDER)){
		$('#riOrderID').val($.cookie(COOKIE_RI_ORDER));
		riOrderDataIDs = $('#riOrderID').val().split(":");
		for(i=0;i<riOrderArray.length;i++){
			for(j=0;j<riOrderDataIDs.length;j++){
				if(riOrderArray[i].id == riOrderDataIDs[j]){
					riOrderData.push({"name":riOrderArray[i].name, "id":riOrderArray[i].id});
					str+= riOrderArray[i].name + ", ";
					newIdList +=  riOrderArray[i].id +":"; // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
				}
			}
		}
		$('#riOrderText').val(str.substr(0,str.length-2));
		$('#riOrderID').val(newIdList.substr(0,newIdList.length-1)); // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
	}

	// 検査ステータス
	str = "";
	newIdList = ""; // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
	if($.cookie(COOKIE_KENSA_STATUS)){
		$('#kensaStatusID').val($.cookie(COOKIE_KENSA_STATUS));
		kensaStatusDataIDs = $('#kensaStatusID').val().split(":");
		for(i=0;i<kensaStatusArray.length;i++){
			for(j=0;j<kensaStatusDataIDs.length;j++){
				if(kensaStatusArray[i].id == kensaStatusDataIDs[j]){
					kensaStatusData.push({"name":kensaStatusArray[i].name, "id":kensaStatusArray[i].id});
					str+= kensaStatusArray[i].name + ", ";
					newIdList +=  kensaStatusArray[i].id +":"; // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
				}
			}
		}
		$('#kensaStatusText').val(str.substr(0,str.length-2));
		$('#kensaStatusID').val(newIdList.substr(0,newIdList.length-1)); // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
	}

	// 依頼科
	str = "";
	newIdList = ""; // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
	if ($.cookie(COOKIE_SECTION)) {
		$('#sectionID').val($.cookie(COOKIE_SECTION));
		sectionIDs = $('#sectionID').val().split(":");
		for (i = 0; i < sectionArray.length; i++) {
			for (j = 0; j < sectionIDs.length; j++) {
				if (sectionArray[i].id == sectionIDs[j]) {
					sectionData.push({"name" : sectionArray[i].name, "id" : sectionArray[i].id});
					str += sectionArray[i].name + ", ";
					newIdList +=  sectionArray[i].id +":"; // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
				}
			}
		}
		$('#sectionText').val(str.substr(0, str.length - 2));
		$('#sectionID').val(newIdList.substr(0,newIdList.length-1)); // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
	}

	// 病棟
	str = "";
	newIdList = ""; // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
	if ($.cookie(COOKIE_BYOUTOU)) {
		$('#byoutouID').val($.cookie(COOKIE_BYOUTOU));
		byoutouIDs = $('#byoutouID').val().split(":");
		for (i = 0; i < byoutouArray.length; i++) {
			for (j = 0; j < byoutouIDs.length; j++) {
				if (byoutouArray[i].id == byoutouIDs[j]) {
					byoutouData.push({"name" : byoutouArray[i].name, "id" : byoutouArray[i].id});
					str += byoutouArray[i].name + ", ";
					newIdList +=  byoutouArray[i].id +":"; // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
				}
			}
		}
		$('#byoutouText').val(str.substr(0, str.length - 2));
		$('#byoutouID').val(newIdList.substr(0,newIdList.length-1)); // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
	}

	// 患者入外
	str = "";
	newIdList = ""; // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
	if ($.cookie(COOKIE_KANJA_NYUGAI)) {
		$('#kanjaNyugaiID').val($.cookie(COOKIE_KANJA_NYUGAI));
		kanjaNyugaiIDs = $('#kanjaNyugaiID').val().split(":");
		for (i = 0; i < kanjaNyugaiArray.length; i++) {
			for (j = 0; j < kanjaNyugaiIDs.length; j++) {
				if (kanjaNyugaiArray[i].id == kanjaNyugaiIDs[j]) {
					kanjaNyugaiData.push({"name" : kanjaNyugaiArray[i].name, "id" : kanjaNyugaiArray[i].id});
					str += kanjaNyugaiArray[i].name + ", ";
					newIdList +=  kanjaNyugaiArray[i].id +":"; // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
				}
			}
		}
		$('#kanjaNyugaiText').val(str.substr(0, str.length - 2));
		$('#kanjaNyugaiID').val(newIdList.substr(0,newIdList.length-1)); // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
	}

	// 伝票入外
	str = "";
	newIdList = ""; // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
	if ($.cookie(COOKIE_DENPYOU_NYUGAI)) {
		$('#denpyouNyugaiID').val($.cookie(COOKIE_DENPYOU_NYUGAI));
		denpyouNyugaiIDs = $('#denpyouNyugaiID').val().split(":");
		for (i = 0; i < denpyouNyugaiArray.length; i++) {
			for (j = 0; j < denpyouNyugaiIDs.length; j++) {
				if (denpyouNyugaiArray[i].id == denpyouNyugaiIDs[j]) {
					denpyouNyugaiData.push({"name" : denpyouNyugaiArray[i].name, "id" : denpyouNyugaiArray[i].id});
					str += denpyouNyugaiArray[i].name + ", ";
					newIdList +=  denpyouNyugaiArray[i].id +":"; // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
				}
			}
		}
		$('#denpyouNyugaiText').val(str.substr(0, str.length - 2));
		$('#denpyouNyugaiID').val(newIdList.substr(0,newIdList.length-1)); // 2014.05.07 Add T.Koudate@COSMO #2682 Cookie不正
	}

	// 2012.01.12 Add Yk.Suzuki@CIJ Start 表示切替
	// 表示切替
	if($.cookie(COOKIE_DISPLAY_TYPE)){
		$('#displayType').val($.cookie(COOKIE_DISPLAY_TYPE));
	}
	// 2012.01.12 Add Yk.Suzuki@CIJ End   表示切替
}

function getDialogButton(dialog_selector, button_name) {
	var buttons = $(dialog_selector + ' .ui-dialog-buttonpane button');
	for (var i = 0; i < buttons.length; i++ ) {
		var button = $(buttons[i]);
		if (button.text() == button_name) {
			return button;
		}
	}

	return null;
}

//2015.02.27 Add S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
/***
 * ヘッダ部とデータ部のスクロールを同期 
 */
function SyncCalendarScroll() {	

	var dateblock = document.getElementById("dateblock");
	var timeblock = document.getElementById("timeblock");
	dateblock.scrollLeft = timeblock.scrollLeft;
}
//2015.02.27 Add S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更