/**
 *
 */
package org.openwis.metadataportal.services.request.dto.subselectionparameters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openwis.harness.subselectionparameters.Parameter;
import org.openwis.metadataportal.services.common.json.SimpleMetadataDTO;
import org.openwis.metadataportal.services.request.dto.submit.SubmitParameterDTO;


/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class GetSubSelectionParametersDTO extends SimpleMetadataDTO {

    private boolean subscription;


    /**
     * Currently selected values for dynamic subselection parameters.
     */
    private Set<SubmitParameterDTO> parameters;

    /**
     /**
     * Gets the isSubscription.
     * @return the isSubscription.
     */
    public boolean isSubscription() {
        return subscription;
    }

    /**
     * Sets the isSubscription.
     * @param isSubscription the isSubscription to set.
     */
    public void setSubscription(boolean subscription) {
        this.subscription = subscription;
    }

    /**
     * Gets the parameters.
     * @return the parameters.
     */
    public Set<SubmitParameterDTO> getParameters() {
        if (parameters == null) {
            parameters = new HashSet<SubmitParameterDTO>();
        }
        return parameters;
    }

    /**
     * Sets the parameters.
     * @param parameters the parameters to set.
     */
    public void setParameters(Set<SubmitParameterDTO> parameters) {
        this.parameters = parameters;
    }


    /**
     * Get the list of Parameter element used for dynamic sub-selection.
     * @return the list of {@link Parameter}
     */
    public List<Parameter> getParameterList() {
        ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
        for (SubmitParameterDTO parameterDto : parameters) {
            Parameter parameter = new Parameter();
            parameter.setCode(parameterDto.getCode());
            parameter.getValues().addAll(parameterDto.getValues());
            parameterList.add(parameter);
        }
        return parameterList;
    }

}
