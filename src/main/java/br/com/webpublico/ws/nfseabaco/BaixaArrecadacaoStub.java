/**
 * BaixaArrecadacaoStub.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.8  Built on : May 19, 2018 (07:06:11 BST)
 */
package br.com.webpublico.ws.nfseabaco;


/*
 *  BaixaArrecadacaoStub java implementation
 */
public class BaixaArrecadacaoStub extends org.apache.axis2.client.Stub {
    private static int counter = 0;
    protected org.apache.axis2.description.AxisOperation[] _operations;

    //hashmaps to keep the fault mapping
    private java.util.HashMap faultExceptionNameMap = new java.util.HashMap();
    private java.util.HashMap faultExceptionClassNameMap = new java.util.HashMap();
    private java.util.HashMap faultMessageMap = new java.util.HashMap();
    private javax.xml.namespace.QName[] opNameArray = null;

    /**
     * Constructor that takes in a configContext
     */
    public BaixaArrecadacaoStub(
            org.apache.axis2.context.ConfigurationContext configurationContext,
            String targetEndpoint) throws org.apache.axis2.AxisFault {
        this(configurationContext, targetEndpoint, false);
    }

    /**
     * Constructor that takes in a configContext  and useseperate listner
     */
    public BaixaArrecadacaoStub(
            org.apache.axis2.context.ConfigurationContext configurationContext,
            String targetEndpoint, boolean useSeparateListener)
            throws org.apache.axis2.AxisFault {
        //To populate AxisService
        populateAxisService();
        populateFaults();

        _serviceClient = new org.apache.axis2.client.ServiceClient(configurationContext,
                _service);

        _serviceClient.getOptions()
                .setTo(new org.apache.axis2.addressing.EndpointReference(
                        targetEndpoint));
        _serviceClient.getOptions().setUseSeparateListener(useSeparateListener);
    }

    /**
     * Default Constructor
     */
    public BaixaArrecadacaoStub(
            org.apache.axis2.context.ConfigurationContext configurationContext)
            throws org.apache.axis2.AxisFault {
        this(configurationContext,
                "http://186.233.148.69:28084/riobranco/servlet/abaixaarrecadacao");
    }

    /**
     * Default Constructor
     */
    public BaixaArrecadacaoStub() throws org.apache.axis2.AxisFault {
        this("http://186.233.148.69:28084/riobranco/servlet/abaixaarrecadacao");
    }

    /**
     * Constructor taking the target endpoint
     */
    public BaixaArrecadacaoStub(String targetEndpoint)
            throws org.apache.axis2.AxisFault {
        this(null, targetEndpoint);
    }

    private static synchronized String getUniqueSuffix() {
        // reset the counter if it is greater than 99999
        if (counter > 99999) {
            counter = 0;
        }

        counter = counter + 1;

        return Long.toString(System.currentTimeMillis()) +
                "_" + counter;
    }

    private void populateAxisService() throws org.apache.axis2.AxisFault {
        //creating the Service with a unique name
        _service = new org.apache.axis2.description.AxisService(
                "BaixaArrecadacao" + getUniqueSuffix());
        addAnonymousOperations();

        //creating the operations
        org.apache.axis2.description.AxisOperation __operation;

        _operations = new org.apache.axis2.description.AxisOperation[1];

        __operation = new org.apache.axis2.description.OutInAxisOperation();

        __operation.setName(new javax.xml.namespace.QName("www.e-nfs.com.br",
                "execute"));
        _service.addOperation(__operation);

        _operations[0] = __operation;
    }

    //populates the faults
    private void populateFaults() {
    }

    /**
     * Auto generated method signature
     *
     * @param baixaArrecadacaoExecute0
     * @see BaixaArrecadacao#execute
     */
    public BaixaArrecadacaoStub.BaixaArrecadacaoExecuteResponse execute(
            BaixaArrecadacaoStub.BaixaArrecadacaoExecute baixaArrecadacaoExecute0)
            throws java.rmi.RemoteException {
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

        try {
            org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0].getName());
            _operationClient.getOptions()
                    .setAction("www.e-nfs.com.braction/ABAIXAARRECADACAO.Execute");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

            addPropertyToOperationClient(_operationClient,
                    org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
                    "&");

            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            env = toEnvelope(getFactory(_operationClient.getOptions()
                            .getSoapVersionURI()),
                    baixaArrecadacaoExecute0,
                    optimizeContent(
                            new javax.xml.namespace.QName("www.e-nfs.com.br",
                                    "execute")),
                    new javax.xml.namespace.QName("www.e-nfs.com.br",
                            "BaixaArrecadacao.Execute"));

            //adding SOAP soap_headers
            _serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            //execute the operation client
            _operationClient.execute(true);

            org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

            Object object = fromOM(_returnEnv.getBody()
                            .getFirstElement(),
                    BaixaArrecadacaoStub.BaixaArrecadacaoExecuteResponse.class);

            return (BaixaArrecadacaoStub.BaixaArrecadacaoExecuteResponse) object;
        } catch (org.apache.axis2.AxisFault f) {
            org.apache.axiom.om.OMElement faultElt = f.getDetail();

            if (faultElt != null) {
                if (faultExceptionNameMap.containsKey(
                        new org.apache.axis2.client.FaultMapKey(
                                faultElt.getQName(), "Execute"))) {
                    //make the fault by reflection
                    try {
                        String exceptionClassName = (String) faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                                faultElt.getQName(), "Execute"));
                        Class exceptionClass = Class.forName(exceptionClassName);
                        java.lang.reflect.Constructor constructor = exceptionClass.getConstructor(String.class);
                        Exception ex = (Exception) constructor.newInstance(f.getMessage());

                        //message class
                        String messageClassName = (String) faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                                faultElt.getQName(), "Execute"));
                        Class messageClass = Class.forName(messageClassName);
                        Object messageObject = fromOM(faultElt,
                                messageClass);
                        java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                new Class[]{messageClass});
                        m.invoke(ex, new Object[]{messageObject});

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    } catch (ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        } finally {
            if (_messageContext.getTransportOut() != null) {
                _messageContext.getTransportOut().getSender()
                        .cleanup(_messageContext);
            }
        }
    }

    /**
     * Auto generated method signature for Asynchronous Invocations
     *
     * @param baixaArrecadacaoExecute0
     * @see BaixaArrecadacao#startexecute
     */
    public void startexecute(
            BaixaArrecadacaoStub.BaixaArrecadacaoExecute baixaArrecadacaoExecute0,
            final BaixaArrecadacaoCallbackHandler callback)
            throws java.rmi.RemoteException {
        org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0].getName());
        _operationClient.getOptions()
                .setAction("www.e-nfs.com.braction/ABAIXAARRECADACAO.Execute");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

        addPropertyToOperationClient(_operationClient,
                org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
                "&");

        // create SOAP envelope with that payload
        org.apache.axiom.soap.SOAPEnvelope env = null;
        final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

        //Style is Doc.
        env = toEnvelope(getFactory(_operationClient.getOptions()
                        .getSoapVersionURI()),
                baixaArrecadacaoExecute0,
                optimizeContent(
                        new javax.xml.namespace.QName("www.e-nfs.com.br", "execute")),
                new javax.xml.namespace.QName("www.e-nfs.com.br",
                        "BaixaArrecadacao.Execute"));

        // adding SOAP soap_headers
        _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        _messageContext.setEnvelope(env);

        // add the message context to the operation client
        _operationClient.addMessageContext(_messageContext);

        _operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
            public void onMessage(
                    org.apache.axis2.context.MessageContext resultContext) {
                try {
                    org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

                    Object object = fromOM(resultEnv.getBody()
                                    .getFirstElement(),
                            BaixaArrecadacaoStub.BaixaArrecadacaoExecuteResponse.class);
                    callback.receiveResultexecute((BaixaArrecadacaoStub.BaixaArrecadacaoExecuteResponse) object);
                } catch (org.apache.axis2.AxisFault e) {
                    callback.receiveErrorexecute(e);
                }
            }

            public void onError(Exception error) {
                if (error instanceof org.apache.axis2.AxisFault) {
                    org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
                    org.apache.axiom.om.OMElement faultElt = f.getDetail();

                    if (faultElt != null) {
                        if (faultExceptionNameMap.containsKey(
                                new org.apache.axis2.client.FaultMapKey(
                                        faultElt.getQName(), "Execute"))) {
                            //make the fault by reflection
                            try {
                                String exceptionClassName = (String) faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(
                                        faultElt.getQName(), "Execute"));
                                Class exceptionClass = Class.forName(exceptionClassName);
                                java.lang.reflect.Constructor constructor = exceptionClass.getConstructor(String.class);
                                Exception ex = (Exception) constructor.newInstance(f.getMessage());

                                //message class
                                String messageClassName = (String) faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(
                                        faultElt.getQName(), "Execute"));
                                Class messageClass = Class.forName(messageClassName);
                                Object messageObject = fromOM(faultElt,
                                        messageClass);
                                java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                        new Class[]{messageClass});
                                m.invoke(ex,
                                        new Object[]{messageObject});

                                callback.receiveErrorexecute(new java.rmi.RemoteException(
                                        ex.getMessage(), ex));
                            } catch (ClassCastException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorexecute(f);
                            } catch (ClassNotFoundException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorexecute(f);
                            } catch (NoSuchMethodException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorexecute(f);
                            } catch (java.lang.reflect.InvocationTargetException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorexecute(f);
                            } catch (IllegalAccessException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorexecute(f);
                            } catch (InstantiationException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorexecute(f);
                            } catch (org.apache.axis2.AxisFault e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorexecute(f);
                            }
                        } else {
                            callback.receiveErrorexecute(f);
                        }
                    } else {
                        callback.receiveErrorexecute(f);
                    }
                } else {
                    callback.receiveErrorexecute(error);
                }
            }

            public void onFault(
                    org.apache.axis2.context.MessageContext faultContext) {
                org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils.getInboundFaultFromMessageContext(faultContext);
                onError(fault);
            }

            public void onComplete() {
                try {
                    _messageContext.getTransportOut().getSender()
                            .cleanup(_messageContext);
                } catch (org.apache.axis2.AxisFault axisFault) {
                    callback.receiveErrorexecute(axisFault);
                }
            }
        });

        org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;

        if ((_operations[0].getMessageReceiver() == null) &&
                _operationClient.getOptions().isUseSeparateListener()) {
            _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
            _operations[0].setMessageReceiver(_callbackReceiver);
        }

        //execute the operation client
        _operationClient.execute(false);
    }

    private boolean optimizeContent(javax.xml.namespace.QName opName) {
        if (opNameArray == null) {
            return false;
        }

        for (int i = 0; i < opNameArray.length; i++) {
            if (opName.equals(opNameArray[i])) {
                return true;
            }
        }

        return false;
    }

    private org.apache.axiom.om.OMElement toOM(
            BaixaArrecadacaoStub.BaixaArrecadacaoExecute param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(BaixaArrecadacaoStub.BaixaArrecadacaoExecute.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
            BaixaArrecadacaoStub.BaixaArrecadacaoExecuteResponse param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(BaixaArrecadacaoStub.BaixaArrecadacaoExecuteResponse.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
            org.apache.axiom.soap.SOAPFactory factory,
            BaixaArrecadacaoStub.BaixaArrecadacaoExecute param,
            boolean optimizeContent, javax.xml.namespace.QName elementQName)
            throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody()
                    .addChild(param.getOMElement(
                            BaixaArrecadacaoStub.BaixaArrecadacaoExecute.MY_QNAME,
                            factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    /* methods to provide back word compatibility */

    /**
     * get the default envelope
     */
    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
            org.apache.axiom.soap.SOAPFactory factory) {
        return factory.getDefaultEnvelope();
    }

    private Object fromOM(org.apache.axiom.om.OMElement param,
                                    Class type) throws org.apache.axis2.AxisFault {
        try {
            if (BaixaArrecadacaoStub.BaixaArrecadacaoExecute.class.equals(
                    type)) {
                return BaixaArrecadacaoStub.BaixaArrecadacaoExecute.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }

            if (BaixaArrecadacaoStub.BaixaArrecadacaoExecuteResponse.class.equals(
                    type)) {
                return BaixaArrecadacaoStub.BaixaArrecadacaoExecuteResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
        } catch (Exception e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

        return null;
    }

    //http://186.233.148.69:28084/riobranco/servlet/abaixaarrecadacao
    public static class GUIAARRECADACAO implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
           name = GUIAARRECADACAO
           Namespace URI = www.e-nfs.com.br
           Namespace Prefix = ns1
         */

        /**
         * field for GRCID
         */
        protected long localGRCID;

        /**
         * field for GRCCTCID
         */
        protected long localGRCCTCID;

        /**
         * field for GRCTRIBUTO
         */
        protected short localGRCTRIBUTO;

        /**
         * field for GRCMESREF
         */
        protected byte localGRCMESREF;

        /**
         * field for GRCANOREF
         */
        protected short localGRCANOREF;

        /**
         * field for GRCDATAVNC
         */
        protected java.util.Date localGRCDATAVNC;

        /**
         * field for GRCVLRDEB
         */
        protected double localGRCVLRDEB;

        /**
         * field for GRCVLRMULTA
         */
        protected double localGRCVLRMULTA;

        /**
         * field for GRCVLRJUROS
         */
        protected double localGRCVLRJUROS;

        /**
         * field for GRCVLRCORRECAO
         */
        protected double localGRCVLRCORRECAO;

        /**
         * field for GRCTIPOMOVIMENTO
         */
        protected byte localGRCTIPOMOVIMENTO;

        /**
         * field for GRCDTMOVIMENTO
         */
        protected java.util.Date localGRCDTMOVIMENTO;

        /**
         * field for GRCCHAVE
         */
        protected String localGRCCHAVE;

        /**
         * field for GRCVLRPAGO
         */
        protected double localGRCVLRPAGO;

        /**
         * Auto generated getter method
         *
         * @return long
         */
        public long getGRCID() {
            return localGRCID;
        }

        /**
         * Auto generated setter method
         *
         * @param param GRCID
         */
        public void setGRCID(long param) {
            this.localGRCID = param;
        }

        /**
         * Auto generated getter method
         *
         * @return long
         */
        public long getGRCCTCID() {
            return localGRCCTCID;
        }

        /**
         * Auto generated setter method
         *
         * @param param GRCCTCID
         */
        public void setGRCCTCID(long param) {
            this.localGRCCTCID = param;
        }

        /**
         * Auto generated getter method
         *
         * @return short
         */
        public short getGRCTRIBUTO() {
            return localGRCTRIBUTO;
        }

        /**
         * Auto generated setter method
         *
         * @param param GRCTRIBUTO
         */
        public void setGRCTRIBUTO(short param) {
            this.localGRCTRIBUTO = param;
        }

        /**
         * Auto generated getter method
         *
         * @return byte
         */
        public byte getGRCMESREF() {
            return localGRCMESREF;
        }

        /**
         * Auto generated setter method
         *
         * @param param GRCMESREF
         */
        public void setGRCMESREF(byte param) {
            this.localGRCMESREF = param;
        }

        /**
         * Auto generated getter method
         *
         * @return short
         */
        public short getGRCANOREF() {
            return localGRCANOREF;
        }

        /**
         * Auto generated setter method
         *
         * @param param GRCANOREF
         */
        public void setGRCANOREF(short param) {
            this.localGRCANOREF = param;
        }

        /**
         * Auto generated getter method
         *
         * @return java.util.Date
         */
        public java.util.Date getGRCDATAVNC() {
            return localGRCDATAVNC;
        }

        /**
         * Auto generated setter method
         *
         * @param param GRCDATAVNC
         */
        public void setGRCDATAVNC(java.util.Date param) {
            this.localGRCDATAVNC = param;
        }

        /**
         * Auto generated getter method
         *
         * @return double
         */
        public double getGRCVLRDEB() {
            return localGRCVLRDEB;
        }

        /**
         * Auto generated setter method
         *
         * @param param GRCVLRDEB
         */
        public void setGRCVLRDEB(double param) {
            this.localGRCVLRDEB = param;
        }

        /**
         * Auto generated getter method
         *
         * @return double
         */
        public double getGRCVLRMULTA() {
            return localGRCVLRMULTA;
        }

        /**
         * Auto generated setter method
         *
         * @param param GRCVLRMULTA
         */
        public void setGRCVLRMULTA(double param) {
            this.localGRCVLRMULTA = param;
        }

        /**
         * Auto generated getter method
         *
         * @return double
         */
        public double getGRCVLRJUROS() {
            return localGRCVLRJUROS;
        }

        /**
         * Auto generated setter method
         *
         * @param param GRCVLRJUROS
         */
        public void setGRCVLRJUROS(double param) {
            this.localGRCVLRJUROS = param;
        }

        /**
         * Auto generated getter method
         *
         * @return double
         */
        public double getGRCVLRCORRECAO() {
            return localGRCVLRCORRECAO;
        }

        /**
         * Auto generated setter method
         *
         * @param param GRCVLRCORRECAO
         */
        public void setGRCVLRCORRECAO(double param) {
            this.localGRCVLRCORRECAO = param;
        }

        /**
         * Auto generated getter method
         *
         * @return byte
         */
        public byte getGRCTIPOMOVIMENTO() {
            return localGRCTIPOMOVIMENTO;
        }

        /**
         * Auto generated setter method
         *
         * @param param GRCTIPOMOVIMENTO
         */
        public void setGRCTIPOMOVIMENTO(byte param) {
            this.localGRCTIPOMOVIMENTO = param;
        }

        /**
         * Auto generated getter method
         *
         * @return java.util.Date
         */
        public java.util.Date getGRCDTMOVIMENTO() {
            return localGRCDTMOVIMENTO;
        }

        /**
         * Auto generated setter method
         *
         * @param param GRCDTMOVIMENTO
         */
        public void setGRCDTMOVIMENTO(java.util.Date param) {
            this.localGRCDTMOVIMENTO = param;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getGRCCHAVE() {
            return localGRCCHAVE;
        }

        /**
         * Auto generated setter method
         *
         * @param param GRCCHAVE
         */
        public void setGRCCHAVE(String param) {
            this.localGRCCHAVE = param;
        }

        /**
         * Auto generated getter method
         *
         * @return double
         */
        public double getGRCVLRPAGO() {
            return localGRCVLRPAGO;
        }

        /**
         * Auto generated setter method
         *
         * @param param GRCVLRPAGO
         */
        public void setGRCVLRPAGO(double param) {
            this.localGRCVLRPAGO = param;
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(
                final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory)
                throws org.apache.axis2.databinding.ADBException {
            return factory.createOMElement(new org.apache.axis2.databinding.ADBDataSource(
                    this, parentQName));
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                              javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                              javax.xml.stream.XMLStreamWriter xmlWriter, boolean serializeType)
                throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(),
                    xmlWriter);

            if (serializeType) {
                String namespacePrefix = registerPrefix(xmlWriter,
                        "www.e-nfs.com.br");

                if ((namespacePrefix != null) &&
                        (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi",
                            "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":GUIAARRECADACAO", xmlWriter);
                } else {
                    writeAttribute("xsi",
                            "http://www.w3.org/2001/XMLSchema-instance", "type",
                            "GUIAARRECADACAO", xmlWriter);
                }
            }

            namespace = "www.e-nfs.com.br";
            writeStartElement(null, namespace, "GRCID", xmlWriter);

            if (localGRCID == Long.MIN_VALUE) {
                throw new org.apache.axis2.databinding.ADBException(
                        "GRCID cannot be null!!");
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        localGRCID));
            }

            xmlWriter.writeEndElement();

            namespace = "www.e-nfs.com.br";
            writeStartElement(null, namespace, "GRCCTCID", xmlWriter);

            if (localGRCCTCID == Long.MIN_VALUE) {
                throw new org.apache.axis2.databinding.ADBException(
                        "GRCCTCID cannot be null!!");
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        localGRCCTCID));
            }

            xmlWriter.writeEndElement();

            namespace = "www.e-nfs.com.br";
            writeStartElement(null, namespace, "GRCTRIBUTO", xmlWriter);

            if (localGRCTRIBUTO == Short.MIN_VALUE) {
                throw new org.apache.axis2.databinding.ADBException(
                        "GRCTRIBUTO cannot be null!!");
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        localGRCTRIBUTO));
            }

            xmlWriter.writeEndElement();

            namespace = "www.e-nfs.com.br";
            writeStartElement(null, namespace, "GRCMESREF", xmlWriter);

            if (localGRCMESREF == Byte.MIN_VALUE) {
                throw new org.apache.axis2.databinding.ADBException(
                        "GRCMESREF cannot be null!!");
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        localGRCMESREF));
            }

            xmlWriter.writeEndElement();

            namespace = "www.e-nfs.com.br";
            writeStartElement(null, namespace, "GRCANOREF", xmlWriter);

            if (localGRCANOREF == Short.MIN_VALUE) {
                throw new org.apache.axis2.databinding.ADBException(
                        "GRCANOREF cannot be null!!");
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        localGRCANOREF));
            }

            xmlWriter.writeEndElement();

            namespace = "www.e-nfs.com.br";
            writeStartElement(null, namespace, "GRCDATAVNC", xmlWriter);

            if (localGRCDATAVNC == null) {
                // write the nil attribute
                throw new org.apache.axis2.databinding.ADBException(
                        "GRCDATAVNC cannot be null!!");
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        localGRCDATAVNC));
            }

            xmlWriter.writeEndElement();

            namespace = "www.e-nfs.com.br";
            writeStartElement(null, namespace, "GRCVLRDEB", xmlWriter);

            if (Double.isNaN(localGRCVLRDEB)) {
                throw new org.apache.axis2.databinding.ADBException(
                        "GRCVLRDEB cannot be null!!");
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        localGRCVLRDEB));
            }

            xmlWriter.writeEndElement();

            namespace = "www.e-nfs.com.br";
            writeStartElement(null, namespace, "GRCVLRMULTA", xmlWriter);

            if (Double.isNaN(localGRCVLRMULTA)) {
                throw new org.apache.axis2.databinding.ADBException(
                        "GRCVLRMULTA cannot be null!!");
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        localGRCVLRMULTA));
            }

            xmlWriter.writeEndElement();

            namespace = "www.e-nfs.com.br";
            writeStartElement(null, namespace, "GRCVLRJUROS", xmlWriter);

            if (Double.isNaN(localGRCVLRJUROS)) {
                throw new org.apache.axis2.databinding.ADBException(
                        "GRCVLRJUROS cannot be null!!");
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        localGRCVLRJUROS));
            }

            xmlWriter.writeEndElement();

            namespace = "www.e-nfs.com.br";
            writeStartElement(null, namespace, "GRCVLRCORRECAO", xmlWriter);

            if (Double.isNaN(localGRCVLRCORRECAO)) {
                throw new org.apache.axis2.databinding.ADBException(
                        "GRCVLRCORRECAO cannot be null!!");
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        localGRCVLRCORRECAO));
            }

            xmlWriter.writeEndElement();

            namespace = "www.e-nfs.com.br";
            writeStartElement(null, namespace, "GRCTIPOMOVIMENTO", xmlWriter);

            if (localGRCTIPOMOVIMENTO == Byte.MIN_VALUE) {
                throw new org.apache.axis2.databinding.ADBException(
                        "GRCTIPOMOVIMENTO cannot be null!!");
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        localGRCTIPOMOVIMENTO));
            }

            xmlWriter.writeEndElement();

            namespace = "www.e-nfs.com.br";
            writeStartElement(null, namespace, "GRCDTMOVIMENTO", xmlWriter);

            if (localGRCDTMOVIMENTO == null) {
                // write the nil attribute
                throw new org.apache.axis2.databinding.ADBException(
                        "GRCDTMOVIMENTO cannot be null!!");
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        localGRCDTMOVIMENTO));
            }

            xmlWriter.writeEndElement();

            namespace = "www.e-nfs.com.br";
            writeStartElement(null, namespace, "GRCCHAVE", xmlWriter);

            if (localGRCCHAVE == null) {
                // write the nil attribute
                throw new org.apache.axis2.databinding.ADBException(
                        "GRCCHAVE cannot be null!!");
            } else {
                xmlWriter.writeCharacters(localGRCCHAVE);
            }

            xmlWriter.writeEndElement();

            namespace = "www.e-nfs.com.br";
            writeStartElement(null, namespace, "GRCVLRPAGO", xmlWriter);

            if (Double.isNaN(localGRCVLRPAGO)) {
                throw new org.apache.axis2.databinding.ADBException(
                        "GRCVLRPAGO cannot be null!!");
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        localGRCVLRPAGO));
            }

            xmlWriter.writeEndElement();

            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(
                String namespace) {
            if (namespace.equals("www.e-nfs.com.br")) {
                return "ns1";
            }

            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix,
                                       String namespace, String localPart,
                                       javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);

            if (writerPrefix != null) {
                xmlWriter.writeStartElement(writerPrefix, localPart, namespace);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix,
                                    String namespace, String attName,
                                    String attValue,
                                    javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);

            if (writerPrefix != null) {
                xmlWriter.writeAttribute(writerPrefix, namespace, attName,
                        attValue);
            } else {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
                xmlWriter.writeAttribute(prefix, namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace,
                                    String attName, String attValue,
                                    javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                xmlWriter.writeAttribute(registerPrefix(xmlWriter, namespace),
                        namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace,
                                         String attName, javax.xml.namespace.QName qname,
                                         javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }

            String attributeValue;

            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(attributePrefix, namespace, attName,
                        attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */
        private void writeQName(javax.xml.namespace.QName qname,
                                javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();

            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);

                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(prefix + ":" +
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                            qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                                 javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }

                    namespaceURI = qnames[i].getNamespaceURI();

                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);

                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":")
                                    .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                            qnames[i]));
                        } else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    qnames[i]));
                        }
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                qnames[i]));
                    }
                }

                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(
                javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace)
                throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();

                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);

                    if ((uri == null) || (uri.length() == 0)) {
                        break;
                    }

                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {
            private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(Factory.class);

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static GUIAARRECADACAO parse(
                    javax.xml.stream.XMLStreamReader reader)
                    throws Exception {
                GUIAARRECADACAO object = new GUIAARRECADACAO();

                int event;
                javax.xml.namespace.QName currentQName = null;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";

                try {
                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    currentQName = reader.getName();

                    if (reader.getAttributeValue(
                            "http://www.w3.org/2001/XMLSchema-instance",
                            "type") != null) {
                        String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                "type");

                        if (fullTypeName != null) {
                            String nsPrefix = null;

                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0,
                                        fullTypeName.indexOf(":"));
                            }

                            nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(
                                    ":") + 1);

                            if (!"GUIAARRECADACAO".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext()
                                        .getNamespaceURI(nsPrefix);

                                return (GUIAARRECADACAO) ExtensionMapper.getTypeObject(nsUri,
                                        type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isEndElement()) {
                        if (reader.isStartElement()) {
                            if (reader.isStartElement() &&
                                    new javax.xml.namespace.QName(
                                            "www.e-nfs.com.br", "GRCID").equals(
                                            reader.getName())) {
                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                        "nil");

                                if ("true".equals(nillableValue) ||
                                        "1".equals(nillableValue)) {
                                    throw new org.apache.axis2.databinding.ADBException(
                                            "The element: " + "GRCID" +
                                                    "  cannot be null");
                                }

                                String content = reader.getElementText();

                                object.setGRCID(org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(
                                        content));

                                reader.next();
                            } // End of if for expected property start element

                            else if (reader.isStartElement() &&
                                    new javax.xml.namespace.QName(
                                            "www.e-nfs.com.br", "GRCCTCID").equals(
                                            reader.getName())) {
                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                        "nil");

                                if ("true".equals(nillableValue) ||
                                        "1".equals(nillableValue)) {
                                    throw new org.apache.axis2.databinding.ADBException(
                                            "The element: " + "GRCCTCID" +
                                                    "  cannot be null");
                                }

                                String content = reader.getElementText();

                                object.setGRCCTCID(org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(
                                        content));

                                reader.next();
                            } // End of if for expected property start element

                            else if (reader.isStartElement() &&
                                    new javax.xml.namespace.QName(
                                            "www.e-nfs.com.br", "GRCTRIBUTO").equals(
                                            reader.getName())) {
                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                        "nil");

                                if ("true".equals(nillableValue) ||
                                        "1".equals(nillableValue)) {
                                    throw new org.apache.axis2.databinding.ADBException(
                                            "The element: " + "GRCTRIBUTO" +
                                                    "  cannot be null");
                                }

                                String content = reader.getElementText();

                                object.setGRCTRIBUTO(org.apache.axis2.databinding.utils.ConverterUtil.convertToShort(
                                        content));

                                reader.next();
                            } // End of if for expected property start element

                            else if (reader.isStartElement() &&
                                    new javax.xml.namespace.QName(
                                            "www.e-nfs.com.br", "GRCMESREF").equals(
                                            reader.getName())) {
                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                        "nil");

                                if ("true".equals(nillableValue) ||
                                        "1".equals(nillableValue)) {
                                    throw new org.apache.axis2.databinding.ADBException(
                                            "The element: " + "GRCMESREF" +
                                                    "  cannot be null");
                                }

                                String content = reader.getElementText();

                                object.setGRCMESREF(org.apache.axis2.databinding.utils.ConverterUtil.convertToByte(
                                        content));

                                reader.next();
                            } // End of if for expected property start element

                            else if (reader.isStartElement() &&
                                    new javax.xml.namespace.QName(
                                            "www.e-nfs.com.br", "GRCANOREF").equals(
                                            reader.getName())) {
                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                        "nil");

                                if ("true".equals(nillableValue) ||
                                        "1".equals(nillableValue)) {
                                    throw new org.apache.axis2.databinding.ADBException(
                                            "The element: " + "GRCANOREF" +
                                                    "  cannot be null");
                                }

                                String content = reader.getElementText();

                                object.setGRCANOREF(org.apache.axis2.databinding.utils.ConverterUtil.convertToShort(
                                        content));

                                reader.next();
                            } // End of if for expected property start element

                            else if (reader.isStartElement() &&
                                    new javax.xml.namespace.QName(
                                            "www.e-nfs.com.br", "GRCDATAVNC").equals(
                                            reader.getName())) {
                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                        "nil");

                                if ("true".equals(nillableValue) ||
                                        "1".equals(nillableValue)) {
                                    throw new org.apache.axis2.databinding.ADBException(
                                            "The element: " + "GRCDATAVNC" +
                                                    "  cannot be null");
                                }

                                String content = reader.getElementText();

                                object.setGRCDATAVNC(org.apache.axis2.databinding.utils.ConverterUtil.convertToDate(
                                        content));

                                reader.next();
                            } // End of if for expected property start element

                            else if (reader.isStartElement() &&
                                    new javax.xml.namespace.QName(
                                            "www.e-nfs.com.br", "GRCVLRDEB").equals(
                                            reader.getName())) {
                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                        "nil");

                                if ("true".equals(nillableValue) ||
                                        "1".equals(nillableValue)) {
                                    throw new org.apache.axis2.databinding.ADBException(
                                            "The element: " + "GRCVLRDEB" +
                                                    "  cannot be null");
                                }

                                String content = reader.getElementText();

                                object.setGRCVLRDEB(org.apache.axis2.databinding.utils.ConverterUtil.convertToDouble(
                                        content));

                                reader.next();
                            } // End of if for expected property start element

                            else if (reader.isStartElement() &&
                                    new javax.xml.namespace.QName(
                                            "www.e-nfs.com.br", "GRCVLRMULTA").equals(
                                            reader.getName())) {
                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                        "nil");

                                if ("true".equals(nillableValue) ||
                                        "1".equals(nillableValue)) {
                                    throw new org.apache.axis2.databinding.ADBException(
                                            "The element: " + "GRCVLRMULTA" +
                                                    "  cannot be null");
                                }

                                String content = reader.getElementText();

                                object.setGRCVLRMULTA(org.apache.axis2.databinding.utils.ConverterUtil.convertToDouble(
                                        content));

                                reader.next();
                            } // End of if for expected property start element

                            else if (reader.isStartElement() &&
                                    new javax.xml.namespace.QName(
                                            "www.e-nfs.com.br", "GRCVLRJUROS").equals(
                                            reader.getName())) {
                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                        "nil");

                                if ("true".equals(nillableValue) ||
                                        "1".equals(nillableValue)) {
                                    throw new org.apache.axis2.databinding.ADBException(
                                            "The element: " + "GRCVLRJUROS" +
                                                    "  cannot be null");
                                }

                                String content = reader.getElementText();

                                object.setGRCVLRJUROS(org.apache.axis2.databinding.utils.ConverterUtil.convertToDouble(
                                        content));

                                reader.next();
                            } // End of if for expected property start element

                            else if (reader.isStartElement() &&
                                    new javax.xml.namespace.QName(
                                            "www.e-nfs.com.br", "GRCVLRCORRECAO").equals(
                                            reader.getName())) {
                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                        "nil");

                                if ("true".equals(nillableValue) ||
                                        "1".equals(nillableValue)) {
                                    throw new org.apache.axis2.databinding.ADBException(
                                            "The element: " + "GRCVLRCORRECAO" +
                                                    "  cannot be null");
                                }

                                String content = reader.getElementText();

                                object.setGRCVLRCORRECAO(org.apache.axis2.databinding.utils.ConverterUtil.convertToDouble(
                                        content));

                                reader.next();
                            } // End of if for expected property start element

                            else if (reader.isStartElement() &&
                                    new javax.xml.namespace.QName(
                                            "www.e-nfs.com.br", "GRCTIPOMOVIMENTO").equals(
                                            reader.getName())) {
                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                        "nil");

                                if ("true".equals(nillableValue) ||
                                        "1".equals(nillableValue)) {
                                    throw new org.apache.axis2.databinding.ADBException(
                                            "The element: " + "GRCTIPOMOVIMENTO" +
                                                    "  cannot be null");
                                }

                                String content = reader.getElementText();

                                object.setGRCTIPOMOVIMENTO(org.apache.axis2.databinding.utils.ConverterUtil.convertToByte(
                                        content));

                                reader.next();
                            } // End of if for expected property start element

                            else if (reader.isStartElement() &&
                                    new javax.xml.namespace.QName(
                                            "www.e-nfs.com.br", "GRCDTMOVIMENTO").equals(
                                            reader.getName())) {
                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                        "nil");

                                if ("true".equals(nillableValue) ||
                                        "1".equals(nillableValue)) {
                                    throw new org.apache.axis2.databinding.ADBException(
                                            "The element: " + "GRCDTMOVIMENTO" +
                                                    "  cannot be null");
                                }

                                String content = reader.getElementText();

                                object.setGRCDTMOVIMENTO(org.apache.axis2.databinding.utils.ConverterUtil.convertToDate(
                                        content));

                                reader.next();
                            } // End of if for expected property start element

                            else if (reader.isStartElement() &&
                                    new javax.xml.namespace.QName(
                                            "www.e-nfs.com.br", "GRCCHAVE").equals(
                                            reader.getName())) {
                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                        "nil");

                                if ("true".equals(nillableValue) ||
                                        "1".equals(nillableValue)) {
                                    throw new org.apache.axis2.databinding.ADBException(
                                            "The element: " + "GRCCHAVE" +
                                                    "  cannot be null");
                                }

                                String content = reader.getElementText();

                                object.setGRCCHAVE(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                        content));

                                reader.next();
                            } // End of if for expected property start element

                            else if (reader.isStartElement() &&
                                    new javax.xml.namespace.QName(
                                            "www.e-nfs.com.br", "GRCVLRPAGO").equals(
                                            reader.getName())) {
                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                        "nil");

                                if ("true".equals(nillableValue) ||
                                        "1".equals(nillableValue)) {
                                    throw new org.apache.axis2.databinding.ADBException(
                                            "The element: " + "GRCVLRPAGO" +
                                                    "  cannot be null");
                                }

                                String content = reader.getElementText();

                                object.setGRCVLRPAGO(org.apache.axis2.databinding.utils.ConverterUtil.convertToDouble(
                                        content));

                                reader.next();
                            } // End of if for expected property start element

                            else {
                                // 3 - A start element we are not expecting indicates an invalid parameter was passed
                                throw new org.apache.axis2.databinding.ADBException(
                                        "Unexpected subelement " +
                                                reader.getName());
                            }
                        } else {
                            reader.next();
                        }
                    } // end of while loop
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        } //end of factory class
    }

    public static class ExtensionMapper {
        public static Object getTypeObject(
                String namespaceURI, String typeName,
                javax.xml.stream.XMLStreamReader reader) throws Exception {
            if ("www.e-nfs.com.br".equals(namespaceURI) &&
                    "GUIAARRECADACAO".equals(typeName)) {
                return GUIAARRECADACAO.Factory.parse(reader);
            }

            if ("www.e-nfs.com.br".equals(namespaceURI) &&
                    "MENSAGENS_RETORNO".equals(typeName)) {
                return MENSAGENS_RETORNO.Factory.parse(reader);
            }

            throw new org.apache.axis2.databinding.ADBException(
                    "Unsupported type " + namespaceURI + " " + typeName);
        }
    }

    public static class MENSAGENS_RETORNO implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
           name = MENSAGENS_RETORNO
           Namespace URI = www.e-nfs.com.br
           Namespace Prefix = ns1
         */

        /**
         * field for CodErro
         */
        protected int localCodErro;

        /**
         * field for MsgErro
         */
        protected String localMsgErro;

        /**
         * Auto generated getter method
         *
         * @return int
         */
        public int getCodErro() {
            return localCodErro;
        }

        /**
         * Auto generated setter method
         *
         * @param param CodErro
         */
        public void setCodErro(int param) {
            this.localCodErro = param;
        }

        /**
         * Auto generated getter method
         *
         * @return java.lang.String
         */
        public String getMsgErro() {
            return localMsgErro;
        }

        /**
         * Auto generated setter method
         *
         * @param param MsgErro
         */
        public void setMsgErro(String param) {
            this.localMsgErro = param;
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(
                final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory)
                throws org.apache.axis2.databinding.ADBException {
            return factory.createOMElement(new org.apache.axis2.databinding.ADBDataSource(
                    this, parentQName));
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                              javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                              javax.xml.stream.XMLStreamWriter xmlWriter, boolean serializeType)
                throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(),
                    xmlWriter);

            if (serializeType) {
                String namespacePrefix = registerPrefix(xmlWriter,
                        "www.e-nfs.com.br");

                if ((namespacePrefix != null) &&
                        (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi",
                            "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":MENSAGENS_RETORNO", xmlWriter);
                } else {
                    writeAttribute("xsi",
                            "http://www.w3.org/2001/XMLSchema-instance", "type",
                            "MENSAGENS_RETORNO", xmlWriter);
                }
            }

            namespace = "";
            writeStartElement(null, namespace, "CodErro", xmlWriter);

            if (localCodErro == Integer.MIN_VALUE) {
                throw new org.apache.axis2.databinding.ADBException(
                        "CodErro cannot be null!!");
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        localCodErro));
            }

            xmlWriter.writeEndElement();

            namespace = "";
            writeStartElement(null, namespace, "MsgErro", xmlWriter);

            if (localMsgErro == null) {
                // write the nil attribute
                throw new org.apache.axis2.databinding.ADBException(
                        "MsgErro cannot be null!!");
            } else {
                xmlWriter.writeCharacters(localMsgErro);
            }

            xmlWriter.writeEndElement();

            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(
                String namespace) {
            if (namespace.equals("www.e-nfs.com.br")) {
                return "ns1";
            }

            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix,
                                       String namespace, String localPart,
                                       javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);

            if (writerPrefix != null) {
                xmlWriter.writeStartElement(writerPrefix, localPart, namespace);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix,
                                    String namespace, String attName,
                                    String attValue,
                                    javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);

            if (writerPrefix != null) {
                xmlWriter.writeAttribute(writerPrefix, namespace, attName,
                        attValue);
            } else {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
                xmlWriter.writeAttribute(prefix, namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace,
                                    String attName, String attValue,
                                    javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                xmlWriter.writeAttribute(registerPrefix(xmlWriter, namespace),
                        namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace,
                                         String attName, javax.xml.namespace.QName qname,
                                         javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }

            String attributeValue;

            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(attributePrefix, namespace, attName,
                        attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */
        private void writeQName(javax.xml.namespace.QName qname,
                                javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();

            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);

                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(prefix + ":" +
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                            qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                                 javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }

                    namespaceURI = qnames[i].getNamespaceURI();

                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);

                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":")
                                    .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                            qnames[i]));
                        } else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    qnames[i]));
                        }
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                qnames[i]));
                    }
                }

                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(
                javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace)
                throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();

                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);

                    if ((uri == null) || (uri.length() == 0)) {
                        break;
                    }

                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {
            private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(Factory.class);

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static MENSAGENS_RETORNO parse(
                    javax.xml.stream.XMLStreamReader reader)
                    throws Exception {
                MENSAGENS_RETORNO object = new MENSAGENS_RETORNO();

                int event;
                javax.xml.namespace.QName currentQName = null;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";

                try {
                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    currentQName = reader.getName();

                    if (reader.getAttributeValue(
                            "http://www.w3.org/2001/XMLSchema-instance",
                            "type") != null) {
                        String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                "type");

                        if (fullTypeName != null) {
                            String nsPrefix = null;

                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0,
                                        fullTypeName.indexOf(":"));
                            }

                            nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(
                                    ":") + 1);

                            if (!"MENSAGENS_RETORNO".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext()
                                        .getNamespaceURI(nsPrefix);

                                return (MENSAGENS_RETORNO) ExtensionMapper.getTypeObject(nsUri,
                                        type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isEndElement()) {
                        if (reader.isStartElement()) {
                            if (reader.isStartElement() &&
                                    new javax.xml.namespace.QName("", "CodErro").equals(
                                            reader.getName())) {
                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                        "nil");

                                if ("true".equals(nillableValue) ||
                                        "1".equals(nillableValue)) {
                                    throw new org.apache.axis2.databinding.ADBException(
                                            "The element: " + "CodErro" +
                                                    "  cannot be null");
                                }

                                String content = reader.getElementText();

                                object.setCodErro(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(
                                        content));

                                reader.next();
                            } // End of if for expected property start element

                            else if (reader.isStartElement() &&
                                    new javax.xml.namespace.QName("", "MsgErro").equals(
                                            reader.getName())) {
                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                        "nil");

                                if ("true".equals(nillableValue) ||
                                        "1".equals(nillableValue)) {
                                    throw new org.apache.axis2.databinding.ADBException(
                                            "The element: " + "MsgErro" +
                                                    "  cannot be null");
                                }

                                String content = reader.getElementText();

                                object.setMsgErro(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                        content));

                                reader.next();
                            } // End of if for expected property start element

                            else {
                                // 3 - A start element we are not expecting indicates an invalid parameter was passed
                                throw new org.apache.axis2.databinding.ADBException(
                                        "Unexpected subelement " +
                                                reader.getName());
                            }
                        } else {
                            reader.next();
                        }
                    } // end of while loop
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        } //end of factory class
    }

    public static class BaixaArrecadacaoExecuteResponse implements org.apache.axis2.databinding.ADBBean {
        public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName("www.e-nfs.com.br",
                "BaixaArrecadacao.ExecuteResponse", "ns1");

        /**
         * field for Mensagens_retorno
         */
        protected MENSAGENS_RETORNO localMensagens_retorno;

        /**
         * Auto generated getter method
         *
         * @return MENSAGENS_RETORNO
         */
        public MENSAGENS_RETORNO getMensagens_retorno() {
            return localMensagens_retorno;
        }

        /**
         * Auto generated setter method
         *
         * @param param Mensagens_retorno
         */
        public void setMensagens_retorno(MENSAGENS_RETORNO param) {
            this.localMensagens_retorno = param;
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(
                final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory)
                throws org.apache.axis2.databinding.ADBException {
            return factory.createOMElement(new org.apache.axis2.databinding.ADBDataSource(
                    this, MY_QNAME));
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                              javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                              javax.xml.stream.XMLStreamWriter xmlWriter, boolean serializeType)
                throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(),
                    xmlWriter);

            if (serializeType) {
                String namespacePrefix = registerPrefix(xmlWriter,
                        "www.e-nfs.com.br");

                if ((namespacePrefix != null) &&
                        (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi",
                            "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":BaixaArrecadacao.ExecuteResponse",
                            xmlWriter);
                } else {
                    writeAttribute("xsi",
                            "http://www.w3.org/2001/XMLSchema-instance", "type",
                            "BaixaArrecadacao.ExecuteResponse", xmlWriter);
                }
            }

            if (localMensagens_retorno == null) {
                throw new org.apache.axis2.databinding.ADBException(
                        "Mensagens_retorno cannot be null!!");
            }

            localMensagens_retorno.serialize(new javax.xml.namespace.QName(
                    "www.e-nfs.com.br", "Mensagens_retorno"), xmlWriter);

            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(
                String namespace) {
            if (namespace.equals("www.e-nfs.com.br")) {
                return "ns1";
            }

            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix,
                                       String namespace, String localPart,
                                       javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);

            if (writerPrefix != null) {
                xmlWriter.writeStartElement(writerPrefix, localPart, namespace);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix,
                                    String namespace, String attName,
                                    String attValue,
                                    javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);

            if (writerPrefix != null) {
                xmlWriter.writeAttribute(writerPrefix, namespace, attName,
                        attValue);
            } else {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
                xmlWriter.writeAttribute(prefix, namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace,
                                    String attName, String attValue,
                                    javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                xmlWriter.writeAttribute(registerPrefix(xmlWriter, namespace),
                        namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace,
                                         String attName, javax.xml.namespace.QName qname,
                                         javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }

            String attributeValue;

            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(attributePrefix, namespace, attName,
                        attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */
        private void writeQName(javax.xml.namespace.QName qname,
                                javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();

            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);

                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(prefix + ":" +
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                            qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                                 javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }

                    namespaceURI = qnames[i].getNamespaceURI();

                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);

                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":")
                                    .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                            qnames[i]));
                        } else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    qnames[i]));
                        }
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                qnames[i]));
                    }
                }

                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(
                javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace)
                throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();

                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);

                    if ((uri == null) || (uri.length() == 0)) {
                        break;
                    }

                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {
            private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(Factory.class);

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static BaixaArrecadacaoExecuteResponse parse(
                    javax.xml.stream.XMLStreamReader reader)
                    throws Exception {
                BaixaArrecadacaoExecuteResponse object = new BaixaArrecadacaoExecuteResponse();

                int event;
                javax.xml.namespace.QName currentQName = null;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";

                try {
                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    currentQName = reader.getName();

                    if (reader.getAttributeValue(
                            "http://www.w3.org/2001/XMLSchema-instance",
                            "type") != null) {
                        String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                "type");

                        if (fullTypeName != null) {
                            String nsPrefix = null;

                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0,
                                        fullTypeName.indexOf(":"));
                            }

                            nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(
                                    ":") + 1);

                            if (!"BaixaArrecadacao.ExecuteResponse".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext()
                                        .getNamespaceURI(nsPrefix);

                                return (BaixaArrecadacaoExecuteResponse) ExtensionMapper.getTypeObject(nsUri,
                                        type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement() &&
                            new javax.xml.namespace.QName("www.e-nfs.com.br",
                                    "Mensagens_retorno").equals(reader.getName())) {
                        object.setMensagens_retorno(MENSAGENS_RETORNO.Factory.parse(
                                reader));

                        reader.next();
                    } // End of if for expected property start element

                    else {
                        // 1 - A start element we are not expecting indicates an invalid parameter was passed
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()) {
                        // 2 - A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                    }
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        } //end of factory class
    }

    public static class BaixaArrecadacaoExecute implements org.apache.axis2.databinding.ADBBean {
        public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName("www.e-nfs.com.br",
                "BaixaArrecadacao.Execute", "ns1");

        /**
         * field for Guiaarrecadacaoiss
         */
        protected GUIAARRECADACAO localGuiaarrecadacaoiss;

        /**
         * Auto generated getter method
         *
         * @return GUIAARRECADACAO
         */
        public GUIAARRECADACAO getGuiaarrecadacaoiss() {
            return localGuiaarrecadacaoiss;
        }

        /**
         * Auto generated setter method
         *
         * @param param Guiaarrecadacaoiss
         */
        public void setGuiaarrecadacaoiss(GUIAARRECADACAO param) {
            this.localGuiaarrecadacaoiss = param;
        }

        /**
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(
                final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory)
                throws org.apache.axis2.databinding.ADBException {
            return factory.createOMElement(new org.apache.axis2.databinding.ADBDataSource(
                    this, MY_QNAME));
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                              javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            serialize(parentQName, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName,
                              javax.xml.stream.XMLStreamWriter xmlWriter, boolean serializeType)
                throws javax.xml.stream.XMLStreamException,
                org.apache.axis2.databinding.ADBException {
            String prefix = null;
            String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();
            writeStartElement(prefix, namespace, parentQName.getLocalPart(),
                    xmlWriter);

            if (serializeType) {
                String namespacePrefix = registerPrefix(xmlWriter,
                        "www.e-nfs.com.br");

                if ((namespacePrefix != null) &&
                        (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi",
                            "http://www.w3.org/2001/XMLSchema-instance", "type",
                            namespacePrefix + ":BaixaArrecadacao.Execute", xmlWriter);
                } else {
                    writeAttribute("xsi",
                            "http://www.w3.org/2001/XMLSchema-instance", "type",
                            "BaixaArrecadacao.Execute", xmlWriter);
                }
            }

            if (localGuiaarrecadacaoiss == null) {
                throw new org.apache.axis2.databinding.ADBException(
                        "Guiaarrecadacaoiss cannot be null!!");
            }

            localGuiaarrecadacaoiss.serialize(new javax.xml.namespace.QName(
                    "www.e-nfs.com.br", "Guiaarrecadacaoiss"), xmlWriter);

            xmlWriter.writeEndElement();
        }

        private static String generatePrefix(
                String namespace) {
            if (namespace.equals("www.e-nfs.com.br")) {
                return "ns1";
            }

            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(String prefix,
                                       String namespace, String localPart,
                                       javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);

            if (writerPrefix != null) {
                xmlWriter.writeStartElement(writerPrefix, localPart, namespace);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(String prefix,
                                    String namespace, String attName,
                                    String attValue,
                                    javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            String writerPrefix = xmlWriter.getPrefix(namespace);

            if (writerPrefix != null) {
                xmlWriter.writeAttribute(writerPrefix, namespace, attName,
                        attValue);
            } else {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
                xmlWriter.writeAttribute(prefix, namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(String namespace,
                                    String attName, String attValue,
                                    javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            } else {
                xmlWriter.writeAttribute(registerPrefix(xmlWriter, namespace),
                        namespace, attName, attValue);
            }
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(String namespace,
                                         String attName, javax.xml.namespace.QName qname,
                                         javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            String attributeNamespace = qname.getNamespaceURI();
            String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }

            String attributeValue;

            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(attributePrefix, namespace, attName,
                        attributeValue);
            }
        }

        /**
         * method to handle Qnames
         */
        private void writeQName(javax.xml.namespace.QName qname,
                                javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            String namespaceURI = qname.getNamespaceURI();

            if (namespaceURI != null) {
                String prefix = xmlWriter.getPrefix(namespaceURI);

                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(prefix + ":" +
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                            qname));
                }
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                                 javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                StringBuffer stringToWrite = new StringBuffer();
                String namespaceURI = null;
                String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }

                    namespaceURI = qnames[i].getNamespaceURI();

                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);

                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite.append(prefix).append(":")
                                    .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                            qnames[i]));
                        } else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    qnames[i]));
                        }
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                qnames[i]));
                    }
                }

                xmlWriter.writeCharacters(stringToWrite.toString());
            }
        }

        /**
         * Register a namespace prefix
         */
        private String registerPrefix(
                javax.xml.stream.XMLStreamWriter xmlWriter,
                String namespace)
                throws javax.xml.stream.XMLStreamException {
            String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();

                while (true) {
                    String uri = nsContext.getNamespaceURI(prefix);

                    if ((uri == null) || (uri.length() == 0)) {
                        break;
                    }

                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }

        /**
         * Factory class that keeps the parse method
         */
        public static class Factory {
            private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(Factory.class);

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
             * If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             * If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static BaixaArrecadacaoExecute parse(
                    javax.xml.stream.XMLStreamReader reader)
                    throws Exception {
                BaixaArrecadacaoExecute object = new BaixaArrecadacaoExecute();

                int event;
                javax.xml.namespace.QName currentQName = null;
                String nillableValue = null;
                String prefix = "";
                String namespaceuri = "";

                try {
                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    currentQName = reader.getName();

                    if (reader.getAttributeValue(
                            "http://www.w3.org/2001/XMLSchema-instance",
                            "type") != null) {
                        String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                "type");

                        if (fullTypeName != null) {
                            String nsPrefix = null;

                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0,
                                        fullTypeName.indexOf(":"));
                            }

                            nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

                            String type = fullTypeName.substring(fullTypeName.indexOf(
                                    ":") + 1);

                            if (!"BaixaArrecadacao.Execute".equals(type)) {
                                //find namespace for the prefix
                                String nsUri = reader.getNamespaceContext()
                                        .getNamespaceURI(nsPrefix);

                                return (BaixaArrecadacaoExecute) ExtensionMapper.getTypeObject(nsUri,
                                        type, reader);
                            }
                        }
                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement() &&
                            new javax.xml.namespace.QName("www.e-nfs.com.br",
                                    "Guiaarrecadacaoiss").equals(reader.getName())) {
                        object.setGuiaarrecadacaoiss(GUIAARRECADACAO.Factory.parse(
                                reader));

                        reader.next();
                    } // End of if for expected property start element

                    else {
                        // 1 - A start element we are not expecting indicates an invalid parameter was passed
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()) {
                        // 2 - A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " + reader.getName());
                    }
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new Exception(e);
                }

                return object;
            }
        } //end of factory class
    }
}
