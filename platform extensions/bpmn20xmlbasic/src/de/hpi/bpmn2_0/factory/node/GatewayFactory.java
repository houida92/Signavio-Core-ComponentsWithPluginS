/*******************************************************************************
 * Signavio Core Components
 * Copyright (C) 2012  Signavio GmbH
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.hpi.bpmn2_0.factory.node;

import java.util.List;

import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.factory.BPMNElement;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.FormalExpression;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.gateway.ComplexGateway;
import de.hpi.bpmn2_0.model.gateway.ConfigurableExclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.ConfigurableInclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.ConfigurableParallelGateway;
import de.hpi.bpmn2_0.model.gateway.EventBasedGateway;
import de.hpi.bpmn2_0.model.gateway.EventBasedGatewayType;
import de.hpi.bpmn2_0.model.gateway.ExclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.Gateway;
import de.hpi.bpmn2_0.model.gateway.GatewayDirection;
import de.hpi.bpmn2_0.model.gateway.InclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.ParallelGateway;

/**
 * The factory to create {@link Gateway} BPMN 2.0 elements
 * 
 * @author Sven Wagner-Boysen
 * 
 */
@StencilId({ 
	"Exclusive_Databased_Gateway",
	"ConfigurableExclusive_Databased_Gateway",
	"ParallelGateway",
	"ConfigurableParallelGateway",
	"EventbasedGateway", 
	"InclusiveGateway", 
	"ConfigurableInclusiveGateway", 
	"ComplexGateway" })
public class GatewayFactory extends AbstractShapeFactory {
	
	// @Override
	public BPMNElement createBpmnElement(GenericShape shape, BPMNElement parent)
			throws BpmnConverterException {
		
		BPMNElement element = super.createBpmnElement(shape, parent);
		if(element.getNode() instanceof ExclusiveGateway) {
			BPMNShape bpmnShape = (BPMNShape) element.getShape();
			String markerVisible = shape.getProperty("markervisible");
			if(markerVisible != null && markerVisible.equals("true")) {
				bpmnShape.setIsMarkerVisible(Boolean.TRUE);
			} else {
				bpmnShape.setIsMarkerVisible(Boolean.FALSE);
			}
		}
		
		return element;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.
	 * oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected BaseElement createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		try {
			Gateway gateway = (Gateway) this.invokeCreatorMethod(shape);
			this.identifyGatewayDirection(gateway, shape);
			return gateway;
		} catch (Exception e) {
			/* Wrap exceptions into specific BPMNConverterException */
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
	}

	/**
	 * Creator method for an exclusive databased Gateway.
	 * 
	 * @param shape
	 *            The resource shape
	 * @return The resulting {@link ExclusiveGateway}
	 */
	@StencilId("Exclusive_Databased_Gateway")
	public ExclusiveGateway createExclusiveGateway(GenericShape shape) {
		ExclusiveGateway gateway = new ExclusiveGateway();
		gateway.setId(shape.getResourceId());
		gateway.setName(shape.getProperty("name"));
		return gateway;
	}
	
	@StencilId("ConfigurableExclusive_Databased_Gateway")
	public ConfigurableExclusiveGateway createConfigurableExclusiveGateway(GenericShape shape) {
		ConfigurableExclusiveGateway gateway = new ConfigurableExclusiveGateway();
		gateway.setId(shape.getResourceId());
		gateway.setName(shape.getProperty("name"));
		return gateway;
	}

	@StencilId("ParallelGateway")
	public ParallelGateway createParallelGateway(GenericShape shape) {
		ParallelGateway gateway = new ParallelGateway();
		gateway.setId(shape.getResourceId());
		gateway.setName(shape.getProperty("name"));
		return gateway;
	}
	
	@StencilId("ConfigurableParallelGateway")
	public ConfigurableParallelGateway createConfigurableParallelGateway(GenericShape shape) {
		ConfigurableParallelGateway gateway = new ConfigurableParallelGateway();
		gateway.setId(shape.getResourceId());
		gateway.setName(shape.getProperty("name"));
		return gateway;
	}
	
	@StencilId("EventbasedGateway")
	public EventBasedGateway createEventBasedGateway(GenericShape shape) {
		EventBasedGateway gateway = new EventBasedGateway();
		
		gateway.setId(shape.getResourceId());
		gateway.setName(shape.getProperty("name"));
		
//		String instantiate = shape.getProperty("instantiate");
//		
//		if(instantiate != null && instantiate.equals("true"))
//			gateway.setInstantiate(true);
//		else
//			gateway.setInstantiate(false);
		
		/* Set gateway type and instantiation */
		gateway.setEventGatewayType(EventBasedGatewayType.EXCLUSIVE);
		gateway.setInstantiate(false);
		
		String type = shape.getProperty("eventtype");
		if(type != null) {
			if(type.equalsIgnoreCase("instantiate_parallel")) {
				gateway.setEventGatewayType(EventBasedGatewayType.PARALLEL);
				gateway.setInstantiate(true);
			} else if(type.equalsIgnoreCase("instantiate_exclusive")) {
				gateway.setEventGatewayType(EventBasedGatewayType.EXCLUSIVE);
				gateway.setInstantiate(true);
			}
		}
		
//		if(type != null && type.equalsIgnoreCase("instantiate_parallel")) 
//			gateway.setEventGatewayType(EventBasedGatewayType.PARALLEL);
//		else 
//			gateway.setEventGatewayType(EventBasedGatewayType.EXCLUSIVE);
		
		return gateway;
	}
	
	@StencilId("InclusiveGateway")
	public InclusiveGateway createInclusiveGateway(GenericShape shape) {
		InclusiveGateway gateway = new InclusiveGateway();
		
		gateway.setId(shape.getResourceId());
		gateway.setName(shape.getProperty("name"));
		
		return gateway;
	}
	
	@StencilId("ConfigurableInclusiveGateway")
	public ConfigurableInclusiveGateway createConfigurableInclusiveGateway(GenericShape shape) {
		ConfigurableInclusiveGateway gateway = new ConfigurableInclusiveGateway();
		
		gateway.setId(shape.getResourceId());
		gateway.setName(shape.getProperty("name"));
		
		return gateway;
	}
	
	@StencilId("ComplexGateway")
	public ComplexGateway createComplexGateway(GenericShape shape) {
		ComplexGateway gateway = new ComplexGateway();
		gateway.setId(shape.getResourceId());
		gateway.setName(shape.getProperty("name"));
		
		String activationCondition = shape.getProperty("activationcondition");
		if(activationCondition != null && !activationCondition.equals("")) {
			gateway.setActivationCondition(new FormalExpression(activationCondition));
		}
		
		return gateway;
	}
	
	/**
	 * Determines and sets the {@link GatewayDirection}
	 */
	private void identifyGatewayDirection(Gateway gateway, GenericShape shape) {

		/* Determine the direction of the Gateway */

		int numIncomming = countSequenceFlows(shape.getIncomingsReadOnly());
		int numOutgoing = countSequenceFlows(shape.getOutgoingsReadOnly());

		GatewayDirection direction = GatewayDirection.UNSPECIFIED;

		if (numIncomming > 1 && numOutgoing > 1)
			direction = GatewayDirection.MIXED;
		else if (numIncomming <= 1 && numOutgoing > 1)
			direction = GatewayDirection.DIVERGING;
		else if (numIncomming > 1 && numOutgoing <= 1)
			direction = GatewayDirection.CONVERGING;

		/* Set the gateway direction */
		gateway.setGatewayDirection(direction);
	}
	
	/**
	 * Counts the number of sequence flows contained in the list.
	 * @param edges
	 * @return
	 */
	private int countSequenceFlows(List<GenericShape> edges) {
		int i = 0;
		
		for(GenericShape edge : edges) {
			if(edge.getStencilId().equals("SequenceFlow")) {
				i++;
			}
		}
		
		return i;
	}

}
