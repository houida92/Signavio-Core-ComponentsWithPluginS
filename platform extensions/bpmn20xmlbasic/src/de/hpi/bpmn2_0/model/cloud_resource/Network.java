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
 * <p>Java class for tNetwork complex type.
 * 
 * @author Souha Boubaker
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tNetwork")
public class Network
extends FlowNode
{
	@XmlAttribute
    @XmlJavaTypeAdapter(EscapingStringAdapter.class)
    protected String name;
	@XmlAttribute
    protected String capacity;
	@XmlAttribute
    protected String nstate;
	
/* Getter & Setter */
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String value) {
        this.capacity = value;
    }
    
    public String getNState() {
        return nstate;
    }

    public void setNState(String value) {
        this.nstate = value;
    }
}
