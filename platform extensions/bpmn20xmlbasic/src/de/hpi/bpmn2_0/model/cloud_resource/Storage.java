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
 * @author Souha Boubaker
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tStorage")
public class Storage
extends FlowNode
{
	@XmlAttribute
    @XmlJavaTypeAdapter(EscapingStringAdapter.class)
    protected String name;
	@XmlAttribute
    protected String size;
	@XmlAttribute
    protected String sstate;
	
 /* Getter & Setter */
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
        return size;
    }

    public void setSize(String value) {
        this.size = value;
    }
    
    public String getSState() {
        return sstate;
    }

    public void setSState(String value) {
        this.sstate = value;
    }
}
