package com.viettel.ems.snmp;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.List;

public class Collector {

    public static void main(String[] args) {
        try {
            Snmp snmp4j = new Snmp(new DefaultUdpTransportMapping());
            snmp4j.listen();
            Address add = new UdpAddress("192.168.1.132" + "/" + "161");
            CommunityTarget<Address> target = new CommunityTarget<>();
            target.setAddress(add);
            target.setTimeout(500);

            target.setRetries(3);

            target.setCommunity(new OctetString("public"));
            target.setVersion(SnmpConstants.version2c);

            PDU request = new PDU();
            request.setType(PDU.GET);
            OID oid = new OID(".1.3.6.1.2.1.1.1.0");
            request.add(new VariableBinding(oid));

            ResponseEvent<Address> responseEvent = snmp4j.send(request, target);

            if (responseEvent == null) {
                return;
            }

            PDU responsePDU = responseEvent.getResponse();
            if (responsePDU == null) {
                return;
            }

            List<? extends VariableBinding> tmpv = responsePDU.getVariableBindings();
            if (tmpv == null) {
                return;
            }

            for (VariableBinding variableBinding : tmpv) {
                if (variableBinding.isException()) {
                    String errorstring = variableBinding.getVariable().getSyntaxString();
                    System.out.println("Error:" + errorstring);
                } else {
                    String sOid = variableBinding.getOid().toString();
                    Variable var = variableBinding.getVariable();
                    OctetString oct = new OctetString((OctetString) var);
                    String sVar = oct.toString();

                    System.out.println("success:" + sVar);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
