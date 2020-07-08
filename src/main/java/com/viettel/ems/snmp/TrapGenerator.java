package com.viettel.ems.snmp;

import lombok.extern.slf4j.Slf4j;
import org.snmp4j.*;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;

@Slf4j
public class TrapGenerator {

    private static final String community = "public"; // SET THIS
    private static final String trapOid = ".1.3.6.1.2.1.1.6";
    private static final String ipAddress = "127.0.0.1"; // SET THIS (this is the destination address)
    private static final int port = 162;

    public static void main(String[] args) {
        // PICK THE VERSION(S) YOU WANT TO SEND
        sendSnmpV1V2Trap(SnmpConstants.version1);
        sendSnmpV1V2Trap(SnmpConstants.version2c);
        sendSnmpV3Trap();
    }

    /** This methods sends the V1/V2 trap */
    private static void sendSnmpV1V2Trap(int version) {
        sendV1orV2Trap(version, community, ipAddress, port);
    }

    private static void sendV1orV2Trap(int snmpVersion, String community, String ipAddress, int port) {
        PDU snmpPDU = createPdu(snmpVersion);

        CommunityTarget<UdpAddress> target = new CommunityTarget<>();
        target.setCommunity(new OctetString(community));
        target.setVersion(snmpVersion);
        target.setAddress(new UdpAddress(ipAddress + "/" + port));
        target.setRetries(2);
        target.setTimeout(5000);

        try (TransportMapping<?> transport = new DefaultUdpTransportMapping(); Snmp snmp = new Snmp(transport)) {
            // Send the PDU
            snmp.send(snmpPDU, target);
            log.info("Sent trap to {}:{}", ipAddress, port);
        } catch (Exception e) {
            log.error("Error in Sending Trap to {}:{}", ipAddress, port, e);
        }
    }

    private static PDU createPdu(int snmpVersion) {
        PDU pdu = snmpVersion == SnmpConstants.version1 ? createPduV1("127.0.0.1", "1.3.6.1.4.1.1824") : createPdu();

        pdu.add(new VariableBinding(SnmpConstants.sysUpTime));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(trapOid)));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress, new IpAddress(ipAddress)));
        pdu.add(new VariableBinding(new OID(trapOid), new OctetString("Major")));
        return pdu;
    }

    private static PDU createPdu() {
        PDU pdu = new PDU();
        pdu.setType(PDU.TRAP);
        pdu.setRequestID(new Integer32(123));
        return pdu;
    }

    private static PDU createPduV1(String ip, String oid) {
        PDUv1 pdu = new PDUv1();
        pdu.setType(PDU.V1TRAP);
        pdu.setEnterprise(new OID(oid));
        pdu.setAgentAddress(new IpAddress(ip)); // SET THIS. This is the sender address
        pdu.setSpecificTrap(5);
        pdu.setGenericTrap(23);
        return pdu;
    }

    /**
     * Sends the v3 trap
     */
    private static void sendSnmpV3Trap() {
        Address targetAddress = GenericAddress.parse("udp:" + ipAddress + "/" + port);
        try (Snmp snmp = new Snmp(new DefaultUdpTransportMapping())) {

            SecurityProtocols protocols = SecurityProtocols.getInstance();
            SecurityProtocols defaultProtocols = protocols.addDefaultProtocols();
            protocols.addPrivacyProtocol(new PrivAES192());
            protocols.addPrivacyProtocol(new PrivAES256());
            protocols.addPrivacyProtocol(new Priv3DES());

            USM usm = new USM(defaultProtocols, new OctetString(MPv3.createLocalEngineID()), 0);
            SecurityModels.getInstance().addSecurityModel(usm);

            //transport.listen();

            String username = "username";
            String authpassphrase = "authpassphrase";
            String privacypassphrase = "privacypassphrase";

            snmp.getUSM().addUser(    //SET THE USERNAME, PROTOCOLS, PASSPHRASES
                new OctetString(username),
                new UsmUser(new OctetString(username), AuthMD5.ID, new OctetString(authpassphrase), PrivAES128.ID,
                    new OctetString(privacypassphrase)));

            // Create Target
            var target = new UserTarget<>();
            target.setAddress(targetAddress);
            target.setRetries(1);
            target.setTimeout(11500);
            target.setVersion(SnmpConstants.version3);
            target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
            target.setSecurityName(new OctetString(username));

            // Create PDU for V3
            ScopedPDU pdu = new ScopedPDU();
            pdu.setType(ScopedPDU.NOTIFICATION);
            pdu.setRequestID(new Integer32(1234));
            pdu.add(new VariableBinding(SnmpConstants.sysUpTime));
            pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, SnmpConstants.linkDown));
            pdu.add(new VariableBinding(new OID(trapOid), new OctetString("Major")));

            // Send the PDU
            snmp.send(pdu, target);
            log.info("Sending Trap to {}:{}", ipAddress, port);
            snmp.addCommandResponder(System.out::println);
        } catch (IOException e) {
            log.error("Error in Sending Trap to {}:{} -> {}", ipAddress, port, e);
        }
    }
}
