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
@XmlType(name = "tAssignment")
public class Assignment
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
	protected int rangecomputemin;
	@XmlAttribute
	protected int rangecomputemax;
	@XmlAttribute
	protected int rangestoragemin;
	@XmlAttribute
	protected int rangestoragemax;
	@XmlAttribute
	protected int rangenetworkmin;
	@XmlAttribute
	protected int rangenetworkmax;
	
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

	public int getRangecomputemin() {
		return rangecomputemin;
	}

	public void setRangecomputemin(int rangecomputemin) {
		this.rangecomputemin = rangecomputemin;
	}

	public int getRangecomputemax() {
		return rangecomputemax;
	}

	public void setRangecomputemax(int rangecomputemax) {
		this.rangecomputemax = rangecomputemax;
	}

	public int getRangestoragemin() {
		return rangestoragemin;
	}

	public void setRangestoragemin(int rangestoragemin) {
		this.rangestoragemin = rangestoragemin;
	}

	public int getRangestoragemax() {
		return rangestoragemax;
	}

	public void setRangestoragemax(int rangestoragemax) {
		this.rangestoragemax = rangestoragemax;
	}

	public int getRangenetworkmin() {
		return rangenetworkmin;
	}

	public void setRangenetworkmin(int rangenetworkmin) {
		this.rangenetworkmin = rangenetworkmin;
	}

	public int getRangenetworkmax() {
		return rangenetworkmax;
	}

	public void setRangenetworkmax(int rangenetworkmax) {
		this.rangenetworkmax = rangenetworkmax;
	}

	
}
