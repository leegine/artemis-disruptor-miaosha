/*
 * Copyright 2000-2005 Fitech Laboratories, Inc. All Rights Reserved.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. FITECH LABORATORIES AND ITS LICENSORS
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL
 * FITECH LABORATORIES OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT
 * OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT
 * OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF FITECH LABORATORIES HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 *
 * $Project:xtier-2.0$
 * $Workfile:ClusterEncoder.java$
 * $Date:10/6/2004 11:01:03 AM$
 * $Revision:6$
 */

package com.fitechlabs.xtier.services.cluster.impl;

import com.fitechlabs.xtier.kernel.*;
import com.fitechlabs.xtier.l10n.*;
import com.fitechlabs.xtier.services.cluster.*;
import com.fitechlabs.xtier.services.marshal.*;
import java.io.*;
import java.net.*;

/**
 * 
 * @author <a href="mailto:dsetraky@fitechlabs.com">Dmitriy Setrakyan</a>
 * @version 2.0
 */
class ClusterEncoder {
    /** */
    static final short TCP_MSG = Short.MAX_VALUE - 2;
    
    /** */
    static final short NODE = Short.MAX_VALUE - 3;
    
    /** */
    static final short GROUP = Short.MAX_VALUE - 4;
    
    /** */
    private ByteMarshaller byteEncoder;
    
    /** */
    private IoMarshaller ioEncoder;
    
    /**
     * 
     * @param config
     */
    ClusterEncoder(final ClusterConfig config) {
        MarshalService marshal = XtierKernel.getInstance().marshal();
        
        marshal.registerFactory(new MarshallableFactory() {
            /**
             * @see MarshallableFactory#tryNewInstance(short)
             */
            public Marshallable tryNewInstance(short typeGuid) {
                switch (typeGuid) {
                    case TCP_MSG: { return new ClusterTcpMsg(); }
                    case NODE: { return new ClusterNodeImpl(config); }
                    case GROUP: { return new ClusterGroupMembershipImpl(); }
                        
                    default: { return null; }
                }
            }
        });
        
        byteEncoder = marshal.getByteMarshaller();
        ioEncoder = marshal.getIoMarshaller();
    }
    
    /**
     * 
     * @param msg
     * @param arr
     * @param off
     * @return TBD
     */
    int encodeUdpMsg(ClusterUdpMsg msg, byte[] arr, int off) {
        byte msgType = msg.getMsgType();
        
        // Encode message type.
        off = byteEncoder.encodeInt8(msgType, arr, off);
        
        switch (msg.getMsgType()) {
            case ClusterUdpMsg.HEARTBEAT: {
                // Encode address.
                off = encodeAddr(msg.getAddress(), arr, off);

                // Encode port.                
                off = byteEncoder.encodeInt32(msg.getPort(), arr, off);
                
                // Encode cpu load.
                off = byteEncoder.encodeFloat32(msg.getCpuLoad(), arr, off);
                
                // Encode total memory.
                off = byteEncoder.encodeInt64(msg.getTotalMemory(), arr, off);
                
                // Encode free memory.
                off = byteEncoder.encodeInt64(msg.getFreeMemory(), arr, off);
                
                break;
            }
                
            case ClusterUdpMsg.DISCOVER: {
                // Encode address.
                off = encodeAddr(msg.getAddress(), arr, off);
                
                // Encode port.
                off = byteEncoder.encodeInt32(msg.getPort(), arr, off);
                
                // Encode message ID.
                off = byteEncoder.encodeInt32(msg.getMsgId(), arr, off);
                
                break;
            }
                
            default: {
                assert false : "Unsupported UDP message type: " + msgType;
            }
        }
        
        return off;
    }
    
    /**
     * 
     * @param msg
     * @param arr
     * @param off
     * @return TBD
     * @throws ClusterException
     */
    int decodeUdpMsg(ClusterUdpMsg msg, byte[] arr, int off) throws ClusterException {
        // Decode message type.
        byte msgType = byteEncoder.decodeInt8(arr, off);
        
        off++;
        
        msg.setMsgType(msgType);
        
        switch (msgType) {
            case ClusterUdpMsg.HEARTBEAT: {
                // Decode address.
                InetAddress addr = decodeAddr(arr, off);
                
                off += getAddrSize(addr);
                
                msg.setAddress(addr);

                // Decode port.
                msg.setPort(byteEncoder.decodeInt32(arr, off));
                
                off += 4;
                
                // Decode cpu load.
                msg.setCpuLoad(byteEncoder.decodeFloat32(arr, off));
                
                off += 4;
                
                // Decode total memory.
                msg.setTotalMemory(byteEncoder.decodeInt64(arr, off));
                
                off += 8;
                
                // Decode free memory.
                msg.setFreeMemory(byteEncoder.decodeInt64(arr, off));
                
                off += 8;
                
                break;
            }
                
            case ClusterUdpMsg.DISCOVER: {
                // Decode address.
                InetAddress addr = decodeAddr(arr, off);
                
                off += getAddrSize(addr);
                
                msg.setAddress(addr);
                
                // Decode port.
                msg.setPort(byteEncoder.decodeInt32(arr, off));
                
                off += 4;
                
                // Decode message id.
                msg.setMsgId(byteEncoder.decodeInt32(arr, off));
                
                off += 4;
                
                break;
            }
                
            default: {
                throw new ClusterException(L10n.format("SRVC.CLUSTER.ERR18", new Byte(msgType)));
            }
        }
        
        return off;
    }
    
    /**
     * 
     * @param msg
     * @param out
     * @throws MarshalException
     * @throws IOException
     */
    void encodeTcpMsg(ClusterTcpMsg msg, OutputStream out) throws MarshalException, IOException {
        ioEncoder.marshalObj(msg, out);
    }
    
    /**
     * 
     * @param in
     * @return TBD
     * @throws MarshalException
     * @throws IOException
     */
    ClusterTcpMsg decodeTcpMsg(InputStream in) throws MarshalException, IOException {
        return (ClusterTcpMsg)ioEncoder.demarshalObj(in);
    }
    
    /**
     * 
     * @param addr
     * @param arr
     * @param off
     * @return TBD
     */
    private int encodeAddr(InetAddress addr, byte[] arr, int off) {
        assert addr != null;

        byte[] bytes = addr.getAddress();
        
        // Encode flag to indicate whether IPv4 of IPv6 address is encoded.
        off = byteEncoder.encodeBool(bytes.length == 4 ? true : false, arr, off);

        System.arraycopy(bytes, 0, arr, off, bytes.length);
        
        off += bytes.length;
        
        return off;
    }
    
    /**
     * 
     * @param arr
     * @param off
     * @return TBD
     * @throws ClusterException
     */
    private InetAddress decodeAddr(byte[] arr, int off) throws ClusterException {
        boolean isIpV4 = byteEncoder.decodeBool(arr, off);
        
        off++;
            
        byte[] bytes = new byte[isIpV4 == true ? 4 : 16];
            
        System.arraycopy(arr, off, bytes, 0, bytes.length);
            
        try {
            return InetAddress.getByAddress(bytes);
        }
        catch (UnknownHostException e) {
            throw new ClusterException(L10n.format("SRVC.CLUSTER.ERR16"), e);
        }
    }
    
    /**
     * 
     * @param out
     * @param val
     * @throws IOException
     */
    void encodeInt64(OutputStream out, long val) throws IOException {
        ioEncoder.encodeInt64(val, out);
    }
    
    /**
     * 
     * @param in
     * @return TBD
     * @throws IOException
     */
    long decodeInt64(InputStream in) throws IOException {
        return ioEncoder.decodeInt64(in);
    }
    
    /**
     * 
     * @param addr
     * @return TBD
     */
    private int getAddrSize(InetAddress addr) {
        return 1 + addr.getAddress().length;
    }
}
