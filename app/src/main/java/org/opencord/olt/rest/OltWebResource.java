/*
 * Copyright 2016-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opencord.olt.rest;

import org.onlab.packet.VlanId;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.DeviceId;
import org.onosproject.net.PortNumber;
import org.onosproject.rest.AbstractWebResource;
import org.opencord.olt.AccessDeviceService;
import org.opencord.olt.AccessSubscriberId;

import java.util.Optional;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * OLT REST APIs.
 */

@Path("oltapp")
public class OltWebResource extends AbstractWebResource {

    /**
     * Provision a subscriber.
     *
     * @param device device id
     * @param port port number
     * @return 200 OK
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{device}/{port}")
    public Response provisionSubscriber(
            @PathParam("device")String device,
            @PathParam("port")long port) {
        AccessDeviceService service = get(AccessDeviceService.class);
        DeviceId deviceId = DeviceId.deviceId(device);
        PortNumber portNumber = PortNumber.portNumber(port);
        ConnectPoint connectPoint = new ConnectPoint(deviceId, portNumber);
        service.provisionSubscriber(connectPoint);
        return ok("").build();
    }

    /**
     * Remove the provisioning for a subscriber.
     *
     * @param device device id
     * @param port port number
     * @return 204 NO CONTENT
     */
    @DELETE
    @Path("{device}/{port}")
    public Response removeSubscriber(
            @PathParam("device")String device,
            @PathParam("port")long port) {
        AccessDeviceService service = get(AccessDeviceService.class);
        DeviceId deviceId = DeviceId.deviceId(device);
        PortNumber portNumber = PortNumber.portNumber(port);
        ConnectPoint connectPoint = new ConnectPoint(deviceId, portNumber);
        service.removeSubscriber(connectPoint);
        return Response.noContent().build();
    }

    /**
     * Provision service for a subscriber.
     *
     * @param portName Name of the port on which the subscriber is connected
     * @return 200 OK or 404 NOT_FOUND
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("services/{portName}")
    public Response provisionServices(
            @PathParam("portName")String portName) {
        AccessDeviceService service = get(AccessDeviceService.class);

        Optional<VlanId> emptyVlan = Optional.empty();
        if (service.provisionSubscriber(new AccessSubscriberId(portName), emptyVlan, emptyVlan)) {
            return ok("").build();
        }
        return Response.status(NOT_FOUND).build();
    }

    /**
     * Provision service with particular tags for a subscriber.
     *
     * @param portName Name of the port on which the subscriber is connected
     * @param sTagVal additional outer tag on this port
     * @param cTagVal additional innter tag on this port
     * @return 200 OK or 404 NOT_FOUND
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("services/{portName}/{sTag}/{cTag}")
    public Response provisionAdditionalVlans(
            @PathParam("portName")String portName,
            @PathParam("sTag")String sTagVal,
            @PathParam("cTag")String cTagVal) {
        AccessDeviceService service = get(AccessDeviceService.class);
        VlanId cTag = VlanId.vlanId(cTagVal);
        VlanId sTag = VlanId.vlanId(sTagVal);

        if (service.provisionSubscriber(new AccessSubscriberId(portName), Optional.of(sTag), Optional.of(cTag))) {
            return ok("").build();
        }
        return Response.status(NOT_FOUND).build();
    }

    /**
     * Removes services for a subscriber.
     *
     * @param portName Name of the port on which the subscriber is connected
     * @return 200 OK or 404 NOT_FOUND
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("services/{portName}")
    public Response deleteServices(
            @PathParam("portName")String portName) {
        AccessDeviceService service = get(AccessDeviceService.class);

        Optional<VlanId> emptyVlan = Optional.empty();
        if (service.removeSubscriber(new AccessSubscriberId(portName), emptyVlan, emptyVlan)) {
            return ok("").build();
        }
        return Response.status(NOT_FOUND).build();
    }

    /**
     * Removes additional vlans of a particular subscriber.
     *
     * @param portName Name of the port on which the subscriber is connected
     * @param sTagVal additional outer tag on this port which needs to be removed
     * @param cTagVal additional inner tag on this port which needs to be removed
     * @return 200 OK or 404 NOT_FOUND
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("services/{portName}/{sTag}/{cTag}")
    public Response removeAdditionalVlans(
            @PathParam("portName")String portName,
            @PathParam("sTag")String sTagVal,
            @PathParam("cTag")String cTagVal) {
        AccessDeviceService service = get(AccessDeviceService.class);
        VlanId cTag = VlanId.vlanId(cTagVal);
        VlanId sTag = VlanId.vlanId(sTagVal);

        if (service.removeSubscriber(new AccessSubscriberId(portName), Optional.of(sTag), Optional.of(cTag))) {
            return ok("").build();
        }
        return Response.status(NOT_FOUND).build();
    }

}
