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
 * <p>Java class for tCompute complex type.
 * 
 * @author Souha Boubaker
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCompute")
public class Compute
extends FlowNode
{

	@XmlAttribute
    @XmlJavaTypeAdapter(EscapingStringAdapter.class)
    protected String name;
	@XmlAttribute
    protected String architecture;
	@XmlAttribute
    protected int cores;
	@XmlAttribute
    protected String hostname;
	@XmlAttribute
    protected String speed;
	@XmlAttribute
    protected String memory;
	@XmlAttribute
    protected String cstate;
	
/* Getter & Setter */
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String value) {
        this.architecture = value;
    }
    

	public int getCores() {
        return cores;
    }

    public void setCores(int value) {
        this.cores = value;
    }

	public String getHostname() {
        return hostname;
    }

    public void setHostname(String value) {
        this.hostname = value;
    }
    
    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String value) {
        this.speed = value;
    }
    
    public String getMemory() {
        return memory;
    }

    public void setMemory(String value) {
        this.memory = value;
    }
    
    public String getCState() {
		return cstate;
	}

	public void setCState(String value) {
		this.cstate = value;
	}
}
