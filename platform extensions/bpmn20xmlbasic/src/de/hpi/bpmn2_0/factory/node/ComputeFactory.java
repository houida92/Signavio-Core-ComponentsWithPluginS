package de.hpi.bpmn2_0.factory.node;

import javax.xml.namespace.QName;

import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.cloud_resource.Compute;
/**
 * The factory for compute
 * 
 * @author Souha Boubaker
 *
 */
@StencilId({"compute"})
public class ComputeFactory extends AbstractShapeFactory {

	@Override
	protected BaseElement createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		
		try {
			Compute compute = new Compute();
			compute.setId(shape.getResourceId());	
			compute.setName(shape.getProperty("name"));
			String architecture = shape.getProperty("architecture");
			compute.setArchitecture(architecture);
			
			System.out.println("---------------test--------------------");
			
			int cores = 0;
			if(!shape.getProperty("cores").equals("")){
				cores =  Integer.parseInt(shape.getProperty("cores"));
				
			}
			compute.setCores(cores);
			
			String hostname = shape.getProperty("hostname");
			compute.setHostname(hostname);
			String speed = shape.getProperty("speed");
			compute.setSpeed(speed);
			String memory = shape.getProperty("memory");
			compute.setMemory(memory);
			String cstate = shape.getProperty("cstate");
			compute.setCState(cstate);
						
				
			return compute;
		} catch (Exception e) {
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
	}
}
