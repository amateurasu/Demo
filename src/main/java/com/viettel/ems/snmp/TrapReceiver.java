package com.viettel.ems.snmp;

import lombok.extern.slf4j.Slf4j;
import org.snmp4j.*;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

import java.io.IOException;
import java.util.List;

@Slf4j
public class TrapReceiver implements CommandResponder {

    private static final String username = "username"; // SET THIS
    private static final String authPassphrase = "authpassphrase"; // SET THIS
    private static final String privacyPassphrase = "privacypassphrase"; // SET THIS

    private Snmp snmp = null;

    public TrapReceiver() { }

    public static void main(String[] args) {
        new TrapReceiver().run();
    }

    private void run() {
        try {
            init();
            snmp.addCommandResponder(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void init() throws IOException {
        var threadPool = ThreadPool.create("Trap", 10);
        var dispatcher = new MultiThreadedMessageDispatcher(threadPool, new MessageDispatcherImpl());

        //TRANSPORT
        Address listenAddress = GenericAddress.parse(System.getProperty("snmp4j.listenAddress", "udp:127.0.0.1/162"));
        TransportMapping<?> transport = listenAddress instanceof UdpAddress
            ? new DefaultUdpTransportMapping((UdpAddress) listenAddress)
            : new DefaultTcpTransportMapping((TcpAddress) listenAddress);

        //V3 SECURITY
        SecurityProtocols protocols = SecurityProtocols.getInstance().addDefaultProtocols();
        USM usm = new USM(protocols, new OctetString(MPv3.createLocalEngineID()), 0);

        SecurityProtocols.getInstance().addPrivacyProtocol(new PrivAES192());
        SecurityProtocols.getInstance().addPrivacyProtocol(new PrivAES256());
        SecurityProtocols.getInstance().addPrivacyProtocol(new Priv3DES());

        usm.setEngineDiscoveryEnabled(true);
        SecurityModels.getInstance().addSecurityModel(usm);

        snmp = new Snmp(dispatcher, transport);
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3(usm));

        snmp.getUSM().addUser(    // SET THE SECURITY PROTOCOLS HERE
            new OctetString(username),
            new UsmUser(new OctetString(username), AuthMD5.ID, new OctetString(authPassphrase), PrivAES128.ID,
                new OctetString(privacyPassphrase)));

        snmp.listen();
    }

    public <A extends Address> void processPdu(CommandResponderEvent<A> crEvent) {
        PDU pdu = crEvent.getPDU();

        switch (pdu.getType()) {
            case PDU.V1TRAP:
                readPduV1(crEvent, (PDUv1) pdu);
                break;
            case PDU.TRAP:
                readPdu(crEvent, pdu);
                break;
        }

        List<? extends VariableBinding> varBinds = pdu.getVariableBindings();
        if (varBinds != null) {
            for (VariableBinding vb : varBinds) {
                Variable variable = vb.getVariable();
                String syntaxString = variable.getSyntaxString();
                int syntax = variable.getSyntax();
                log.info("Variables{OID={}, value={}, syntax={}, syntaxString={}}; ", vb.getOid(), variable, syntax,
                    syntaxString);
            }
        }

        System.out.println("==== TRAP END ===");
    }

    private static <A extends Address> void readPdu(CommandResponderEvent<A> crEvent, PDU pdu) {
        log.info("SnmpTrap{errorStatus={}, errorIndex={}, requestID={}, snmpVersion={}, community={}}",
            pdu.getErrorStatus(), pdu.getErrorIndex(), pdu.getRequestID(), PDU.TRAP,
            new String(crEvent.getSecurityName()));
    }

    private static <A extends Address> void readPduV1(CommandResponderEvent<A> crEvent, PDUv1 pdu) {
        log.info("SnmpTrapV1{agentAddr={}, enterprise={}, timeStamp={}, genericTrap={}, specificTrap={}, community={}}",
            pdu.getAgentAddress(), pdu.getEnterprise(), pdu.getTimestamp(), pdu.getGenericTrap(), pdu.getSpecificTrap(),
            new String(crEvent.getSecurityName()));
    }
}
