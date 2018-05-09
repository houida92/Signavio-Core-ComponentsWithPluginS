/**
 * 
 * @author Ahmed Fathallah
 * 
 */

if (!ORYX.Plugins)
	ORYX.Plugins = new Object();

function XMLWriter() {
	this.XML = [];
	this.Nodes = [];
	this.State = "";
	this.FormatXML = function(Str) {
		if (Str)
			return Str.replace(/&/g, "&amp;").replace(/\"/g, "&quot;").replace(
					/</g, "&lt;").replace(/>/g, "&gt;");
		return ""
	}
	this.BeginNode = function(Name) {
		if (!Name)
			return;
		if (this.State == "beg")
			this.XML.push(">");
		this.State = "beg";
		this.Nodes.push(Name);
		this.XML.push("<" + Name);
	}
	this.EndNode = function() {
		if (this.State == "beg") {
			this.XML.push("/>");
			this.Nodes.pop();
		} else if (this.Nodes.length > 0)
			this.XML.push("</" + this.Nodes.pop() + ">");
		this.State = "";
	}
	this.Attrib = function(Name, Value) {
		if (this.State != "beg" || !Name)
			return;
		this.XML.push(" " + Name + "=\"" + this.FormatXML(Value) + "\"");
	}
	this.WriteString = function(Value) {
		if (this.State == "beg")
			this.XML.push(">");
		this.XML.push(this.FormatXML(Value));
		this.State = "";
	}
	this.Node = function(Name, Value) {
		if (!Name)
			return;
		if (this.State == "beg")
			this.XML.push(">");
		this.XML.push((Value == "" || !Value) ? "<" + Name + "/>" : "<" + Name
				+ ">" + this.FormatXML(Value) + "</" + Name + ">");
		this.State = "";
	}
	this.Close = function() {
		while (this.Nodes.length > 0)
			this.EndNode();
		this.State = "closed";
	}
	this.ToString = function() {
		return this.XML.join("");
	}
};

ORYX.Plugins.Configuration = Clazz.extend({

	// Defines the facade
	facade : undefined,

	// Constructor
	construct : function(facade) {

		this.facade = facade;

		// Offers the functionality of undo
		this.facade.offer({
			'name' : ORYX.I18N.Configuration.configuration,
			'description' : ORYX.I18N.Configuration.configurationAltDesc,
			'icon' : ORYX.PATH + "images/configuration/configuration_icon.png",
			'functionality' : this.doConfig.bind(this),
			'group' : ORYX.I18N.Configuration.group
		});
	},

	handleExecuteCommands : function(evt) {

		// ...

	},

	findAllElements : function(XML, children) {
		for (var i = 0; i < children.length; i++) {
			if (children[i].stencil.id == 'Pool'
					|| children[i].stencil.id == 'Lane') {
				this.findAllElements(XML, children[i].childShapes);
			} else {
				XML.BeginNode(children[i].stencil.id);
				XML.Attrib("name", children[i].properties.name);
				XML.Attrib("resourceId", children[i].resourceId);
				if (children[i].outgoing.length > 0) {
					for (var j = 0; j < children[i].outgoing.length; j++) {
						XML.BeginNode("Outgoing");
						XML.Attrib("resourceId",
								children[i].outgoing[j].resourceId);
						// XML.Attrib("stencilId",
						// children[i].outgoing[j].stencil.id);
						XML.EndNode();
					}
				}
				XML.EndNode();
			}
		}
	},

	doConfig : function() {
		
		var modelMeta = this.facade.getModelMetaData();
		var reqURI = modelMeta.modelHandler;
		var reqURIs = reqURI.split("/");
		var prefix = "/";
		for (i = 1; i < reqURIs.length - 1; i++) {
			prefix += reqURIs[i] + "/";
		}

		var jsonObj = this.facade.getJSON();
		var XML = new XMLWriter();

		XML.BeginNode("Process");
		this.findAllElements(XML, jsonObj.childShapes);
		XML.Close();

		var txt = XML.ToString();
		var resJSON;

		// Create form
		var formPanel = new Ext.form.FormPanel({
			id : 'query_model',
			bodyStyle : 'padding:10px',
			width : 'auto',
			height : 'auto',
			items : [ new Ext.form.NumberField({
				fieldLabel : 'Support',
				name : 'support',
				id : 'support',
				allowBlank : false,
				width : 250,
				emptyText : '[0,1]',
				valueField : 'myId',
				displayField : 'myText',
				mode : 'local',
				minValue : 0,
				maxValue : 1
			}), new Ext.form.NumberField({
				fieldLabel : 'Confidence',
				name : 'confidence',
				id : 'confidence',
				allowBlank : false,
				width : 250,
				emptyText : '[0,1]',
				valueField : 'myId',
				displayField : 'myText',
				mode : 'local',
				minValue : 0,
				maxValue : 1
			}), new Ext.form.NumberField({
				fieldLabel : 'Rules numbers',
				name : 'nbRules',
				id : 'nbRules',
				allowBlank : false,
				width : 250,
				emptyText : '0',
				valueField : 'myId',
				displayField : 'myText',
				mode : 'local',
				minValue : 1
			}) ]
		});

		// Create new window and attach form into it
		var win = new Ext.Window({
			id : 'Query_Window',
			width : 'auto',
			height : 'auto',
			title : "Algorithm parameters",
			modal : true,
			resizable : false,
			bodyStyle : 'background:#FFFFFF',
			items : [ formPanel ],
			defaultButton : 0,
			buttons : [ {
				text : "Validate",
				handler : function() {

					win.body.mask("Please Wait", "x-waiting-box");

					window.setTimeout(function() {

						callback(formPanel.getForm());

					}.bind(this), 10);
				},
				listeners : {
					render : function() {
						this.focus();
					}
				}
			}, {
				text : ORYX.I18N.Save.close,
				handler : function() {
					win.close();
				}.bind(this)
			} ],
			listeners : {
				close : function() {
					win.destroy();
					delete this.saving;
				}.bind(this)
			}
		});

		win.show();

		// Create the callback for the template
		callback = function(form) {

			var support = form.findField('support').getValue();
			var confidence = form.findField('confidence').getValue();
			var nbRules = form.findField('nbRules').getValue();

			new Ajax.Request(prefix + 'configurationrule/', {
				method : 'get',
				asynchronous : true,
				requestHeaders : {
					"Accept" : "application/json"
				},
				parameters : {
					xml : txt,
					support : support,
					confidence : confidence,
					nbRules : nbRules
				},
				encoding : 'UTF-8',
				onSuccess : successQuery,
				onException : function(req, exception) {
					/*
					dsException = "\n\n";

					dsException += "exception.name: ";
					dsException += exception.name

					dsException += "\n";
					dsException += "exception.number: ";
					dsException += exception.number;

					dsException += "\n";
					dsException += "exception.description: ";
					dsException += exception.description;

					dsException += "\n";
					dsException += "exception.stack: ";
					dsException += exception.stack;

					alert(dsException);*/
					Ext.Msg.alert(ORYX.I18N.Oryx.title,
							ORYX.I18N.Configuration.getFailure).setIcon(
							Ext.Msg.WARNING).getDialog().setWidth(260).center()
							.syncSize();
				}.bind(this),
				onFailure : (function(transport) {
					Ext.Msg.alert(ORYX.I18N.Oryx.title,
							ORYX.I18N.Configuration.getFailure).setIcon(
							Ext.Msg.WARNING).getDialog().setWidth(260).center()
							.syncSize();
				}).bind(this),
				on401 : (function(transport) {
					Ext.Msg.alert(ORYX.I18N.Oryx.title,
							ORYX.I18N.Configuration.getFailure).setIcon(
							Ext.Msg.WARNING).getDialog().setWidth(260).center()
							.syncSize();
				}).bind(this),
				on403 : (function(transport) {
					Ext.Msg.alert(ORYX.I18N.Oryx.title,
							ORYX.I18N.Configuration.getFailure).setIcon(
							Ext.Msg.WARNING).getDialog().setWidth(260).center()
							.syncSize();
				}).bind(this)
			});
		}.bind(this);

		var successQuery = function(transport) {
			resJSON = transport.responseText.evalJSON();
			
			win.close();
			
			var rt = Ext.data.Record.create([{
			    name: 'rule'
			}, {
			    name: 'support'
			}, {
			    name: 'confidence'
			}])
			var resultStore = new Ext.data.Store({
			        isAutoLoad: true,
			        reader: new Ext.data.JsonReader({
			            root: 'results',
			            fields: [{
			                name: 'rule',
			                mapping: 'rule'
			            }, {
			                name: 'support',
			                mapping: 'support'
			            }, {
			                name: 'confidence',
			                mapping: 'confidence'
			            }]
			        }, rt)
			    }) 

			resultStore.loadData(resJSON); 

			var grid = new Ext.grid.GridPanel({
			    id: 'grid_results',
			    store: resultStore,
			    autoScroll: true,
			    colModel: new
			    Ext.grid.ColumnModel({
			        defaultSortable: true,
			        defaults: {
			            sortable: true
			        },

			        columns: [{
			            id: 'rule',
			            header: 'Rule',
			            dataIndex: 'rule',
			            type: 'string'
			        }, {
			            id: 'support',
			            width: 25,
			            header: 'Support', 
			            dataIndex : 'support', 
			            type : 'double' 
			        }, { 
			        	id : 'confidence',
			        	width: 25,
			            header: 'Confidence',
			            dataIndex: 'confidence',
			            type: 'double'
			        }]
			    }),
			    viewConfig: {
			        forceFit: true,
			    },
			    sm: new Ext.grid.RowSelectionModel({
			        singleSelect: true,
			        listeners: {}
			    }),
			    width: 1200,
			    height: 500,
			    frame: true,
			    layout: 'fit',
			    iconCls: 'icon-grid'
			});

			var panel = new Ext.Panel({
			    layout: 'border',
			    defaults: {
			        collapsible: true,
			        split: true
			    },
			    width: 1200,
			    height: 500,
			    items: [{
			        collapsible: false,
			        region: 'center',
			        margins: '5 0 0 0',
			        items: [grid]
			    }]
			});

			// Create new window and attach grid results into it
			var winResults = new Ext.Window({
			    id: 'Query_Result_Window',
			    width: 'auto',
			    height: 'auto',
			    title: "Configuration Rules",
			    modal: true,
			    resizable: false,
			    bodyStyle: 'background:#FFFFFF',
			    items: [panel],
			    defaultButton: 0,
			    buttons: [{
					text : "Generate configuration system",
					handler : function() {

						win.body.mask("Please Wait", "x-waiting-box");

						window.setTimeout(function() {

							// Create form
							var formPanel2 = new Ext.form.FormPanel({
								id : 'graph_model',
								bodyStyle : 'padding:10px',
								width : 'auto',
								height : 'auto',
								overflow:'auto',
								items : [ {
						            html: "<div id='canvas'></div>",
						            xtype: "panel"} ]
							});

							// Create new window and attach form into it
							var win2 = new Ext.Window({
								id : 'Query2_Window',
								width : 'auto',
								height : 'auto',
								title : "Petri Net",
								modal : true,
								autoScroll: true,
								resizable : true,
								bodyStyle : 'background:#FFFFFF',
								items : [ formPanel2 ],
								defaultButton : 0,
								buttons : [ {
									text : ORYX.I18N.Save.close,
									handler : function() {
										win2.close();
									}.bind(this)
								} ],
								listeners : {
									close : function() {
										win2.destroy();
										delete this.saving;
									}.bind(this)
								}
							});

							win2.show();
							
							var renderTransition = function(r, n) {
					            /* the Raphael set is obligatory, containing all you want to display */
					            var set = r.set().push(
					                /* custom objects go here */
					                r.rect(n.point[0], n.point[1], 40, 24).attr({"fill": "#eee", r : "1px", "stroke-width" : n.distance == 0 ? "3px" : "1px" })).push(
					                r.text(n.point[0]+20, n.point[1]+10, (n.label || n.id) + "\n"));
					            	set.items.forEach(function(el) {el.tooltip(r.set().push(r.text(-20, -60, n.title + "\n").attr({"font-weight":"bold"})))});
					            return set;
					        };
							
					        var renderPlace = function(r, n) {
					            /* the Raphael set is obligatory, containing all you want to display */
					            var set = r.set().push(
					                /* custom objects go here */
					                r.circle(n.point[0]-30, n.point[1]-13, 0).attr({"fill": "#eee", r : "15px", "stroke-width" : n.distance == 0 ? "3px" : "1px" })).push(
					                r.text(n.point[0]-30, n.point[1]-13, (n.label || n.id) + "\n"));
					            return set;
					        };
							
							//Draw Graph
							var g = new Graph();
							for (var i = 0; i < resJSON.petrinet.length; i++) {
							    var item = resJSON.petrinet[i];
							    
							    if(item.source.includes("G")){
							    	var lb = item.labelS.replace(/&lt;/g, "<");
							    	lb = lb.replace(/&gt;/g, ">");
							    	g.addNode(item.source, {render:renderTransition, title:lb});
							    }
							    else
									g.addNode(item.source, {render:renderPlace});
							    
							    if(item.target.includes("G")){
							    	var lb = item.labelT.replace(/&lt;/g, "<");
							    	lb = lb.replace(/&gt;/g, ">");
							    	g.addNode(item.target, {render:renderTransition, title:lb});
							    }
							    else
									g.addNode(item.target, {render:renderPlace});
								
							    g.addEdge(item.source, item.target, { directed : true } );
							}

							var layouter = new Graph.Layout.Spring(g);
							layouter.layout();
							 
							var renderer = new Graph.Renderer.Raphael('canvas', g, 1200, 500);
							renderer.draw();

						}.bind(this), 10);
					},
					listeners : {
						render : function() {
							this.focus();
						}
					}
				},{
			        text: ORYX.I18N.Save.close,
			        handler: function() {
			            winResults.close();
			        }.bind(this)
			    }],
			    listeners: {
			        close: function() {
			            winResults.destroy();
			            delete
			            this.saving;
			        }.bind(this)
			    }
			});

			winResults.show();

			Ext.get("grid_results").fadeIn({
			    endOpacity: 1,
			    duration: 1
			});
				
		}.bind(this);
	}
});