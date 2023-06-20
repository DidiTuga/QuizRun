/**
 * @file BluetoothManager.java
 * @brief Classe para gerir a conexão Bluetooth
 * @date 15/06/2023
 * @version 1.1
 * @autor Diogo Santos nº45842
 */

package di.ubi.quizrun;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Classe para conectar ao dispositivo Bluetooth
 * Nessa conexão é possível enviar e receber dados
 */
public class BluetoothManager {
    private static final int MY_PERMISSIONS_REQUEST_BLUETOOTH = 1;
    private final BluetoothAdapter mAdapter;
    private final Context mContext;
    private final UUID mUUID;
    private final Handler mHandler;
    private final Activity mActivity;
    private BluetoothSocket mSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private boolean mIsConnected;
    private String mDeviceName;

    /**
     * Construtor
     * @param context - Contexto da aplicação
     * @param handler - Handler para enviar as mensagens para a UI
     * @param mActivity - Activity onde está a ser usado
     */
    public BluetoothManager(Context context, Handler handler, Activity mActivity) {
        mContext = context;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID padrão para Bluetooth SPP
        mIsConnected = false;
        mHandler = handler;
        this.mActivity = mActivity;
    }


    /**
     * Procurar por dispositivos Bluetooth pareados
     * E conectar ao dispositivo com o nome passado como parâmetro
     * Se não encontrar nenhum dispositivo com esse nome, envia uma mensagem para a UI, a dizer que não encontrou e coloca a janela de definições do Bluetooth
     * @param DeviceName - Nome do dispositivo a procurar
     */
    public void connectToDevice(String DeviceName) {
        mDeviceName = DeviceName;
        // Verificar se a permissão de acesso ao Bluetooth foi concedida
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, MY_PERMISSIONS_REQUEST_BLUETOOTH);
        }
        // bluetooth admin
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, MY_PERMISSIONS_REQUEST_BLUETOOTH);
        }
        // bluetooth
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH}, MY_PERMISSIONS_REQUEST_BLUETOOTH);
        }
        try {
            BluetoothDevice device_final = null;
            if (mAdapter.isEnabled()) {
                Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    Uteis.MSG_Log("Dispositivos pareados: " + pairedDevices.size());
                    for (BluetoothDevice device : pairedDevices) {
                        Uteis.MSG_Log("Device: " + device.getName() + ", " + device);
                        if (device.getName().contains(DeviceName)) {
                            device_final = device;
                            break;
                        }
                    }
                }
                if (device_final == null) {
                    Uteis.MSG(mContext, "Dispositivo não encontrado, conecte-se ao dispositivo");
                    sleep(1000);
                    Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    mContext.startActivity(intent);
                } else {
                    mSocket = device_final.createRfcommSocketToServiceRecord(mUUID);
                    mSocket.connect();
                    // quando o socket já estiver ocupado por outra conexão

                    mInputStream = mSocket.getInputStream();
                    mOutputStream = mSocket.getOutputStream();
                    Uteis.MSG_Log("Conectado com o dispositivo: " + device_final.getName());
                    mIsConnected = true;
                    mDeviceName = device_final.getName();
                    startListening();
                    // se estiver conectado, enviar mensagem para a MainActivity atraves do Handler
                    String msg = "c";
                    sendData(msg);
                }
            } else {
                Uteis.MSG(mContext, "Ative o bluetooth e faça a conexão ao dispositivo");
                sleep(1000);
                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                mContext.startActivity(intent);
            }
        } catch (IOException e) {
            Uteis.MSG_Debug("Erro ao conectar com o dispositivo: " + e.getMessage());
            mIsConnected = false;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Enviar dados para o dispositivo Bluetooth
     * @param data - dados a serem enviados
     */
    public synchronized void sendData(String data) {
        Uteis.MSG_Log("Tentar enviar data: " + data);
        while (!mIsConnected) {
            connectToDevice(mDeviceName);
        }
        try {

            mOutputStream.write(data.getBytes());
            mOutputStream.flush();
            Uteis.MSG_Log("Enviado data com sucesso!");
        } catch (IOException e) {
            Uteis.MSG_Debug("Erro ao enviar dados para o dispositivo: " + e.getMessage());
        }

    }

    /**
     * Escuta os dados recebidos do dispositivo Bluetooth
     * os dados são enviados para a MainActivity atraves do Handler
     */
    public void startListening() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                int bytes;
                while (mIsConnected) {
                    try {
                        bytes = mInputStream.read(buffer);
                        String incomingMessage = new String(buffer, 0, bytes);
                        Uteis.MSG_Log("Recebido: " + incomingMessage);
                        // Enviar mensagem para a MainActivity atraves do Handler
                        mHandler.obtainMessage(23, bytes, -1, incomingMessage).sendToTarget();
                        // limpar o buffer
                        buffer = new byte[1024];

                    } catch (IOException e) {
                        Uteis.MSG_Debug("Erro ao receber dados do dispositivo: " + e.getMessage());
                        mIsConnected = false;
                        // Tentar reconectar
                        connectToDevice(mDeviceName);
                    }
                }
            }
        });
        thread.start();
    }

    /**
     * Parar de escutar os dados recebidos do dispositivo Bluetooth
     */
    public void stopListening() {
        mIsConnected = false;
    }

    /**
     * Desconectar do dispositivo Bluetooth
     */
    public void disconnect() {
        if (mIsConnected) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mIsConnected = false;
            mDeviceName = null;
        }
    }

    /**
     * @return - retorna o nome do dispositivo Bluetooth
     */
    public String getDeviceName() {
        return mDeviceName;
    }

    /**
     * Coloca um novo dispositivo Bluetooth para ser conectado
     * @param deviceName - nome do dispositivo Bluetooth
     */
    public void setDeviceName(String deviceName) {
        mDeviceName = deviceName;
    }

    /**
     * Função para dar return aos devices pareados
     *
     * @return Array de Strings com os nomes dos devices pareados
     */
    public String[] getpairedDevices() {
        // Verificar se a permissão de acesso ao Bluetooth foi concedida
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, MY_PERMISSIONS_REQUEST_BLUETOOTH);
        }
        // bluetooth admin
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, MY_PERMISSIONS_REQUEST_BLUETOOTH);
        }
        // bluetooth
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH}, MY_PERMISSIONS_REQUEST_BLUETOOTH);
        }
        Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();

        String[] pairedD = new String[pairedDevices.size()];
        if (pairedDevices.size() > 0) {
            Uteis.MSG_Log("Dispositivos pareados: " + pairedDevices.size());
            for (int i = 0; i < pairedDevices.size(); i++) {
                if (!pairedDevices.toArray()[i].equals(mDeviceName)) {
                    pairedD[i] = pairedDevices.toArray()[i].toString();
                }
            }
        }
        return pairedD;
    }

    /**
     * @return - retorna o estado da conexão
     */
    public boolean isConnected() {
        return mIsConnected;
    }
}
