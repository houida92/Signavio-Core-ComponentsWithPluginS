package de.hpi.bpmn2_0.model.cloud_resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.util.EscapingStringAdapter;

/**
 * <p>Java class for tStorage complex type.
 * 
 * @author Ahmed Fathallah
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tElasticity")
public class Elasticity
extends FlowNode
{
	@XmlAttribute
    @XmlJavaTypeAdapter(EscapingStringAdapter.class)
    protected String name;
	@XmlAttribute
	protected String type;
	@XmlAttribute
	protected String configurable;
	@XmlAttribute
	protected String logicaltype;
	
	
 /* Getter & Setter */
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getConfigurable() {
		return configurable;
	}

	public void setConfigurable(String configurable) {
		this.configurable = configurable;
	}

	public String getLogicaltype() {
		return logicaltype;
	}

	public void setLogicaltype(String logicaltype) {
		this.logicaltype = logicaltype;
	}

	
	
}
