package com.combination;

import com.leff.midi.event.MidiEvent;
import com.leff.midi.MidiFile;
import com.leff.midi.util.MidiEventListener;
import com.leff.midi.util.MidiProcessor;


import javax.sound.midi.*;
import javax.swing.*;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main implements MidiEventListener, ActionListener {

    static Receiver receiver ;
    static int channel = -1 ;
    private JFrame mainUI ;
    private String midiFilePath ;
    private CopyOnWriteArrayList<ChannelSwitch> receivers = new CopyOnWriteArrayList<>();
    private Main(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("select a midi file");
        fileChooser.addActionListener(e -> {
            if(midiFilePath == null){
                midiFilePath = fileChooser.getSelectedFile().getAbsolutePath() ;
                mainUI = new JFrame("MidiPlayer");
                JButton newChannel = new JButton("Create new a Channel Player, and Select a sf2 file");
                newChannel.addActionListener(this);
                mainUI.add(newChannel);
                mainUI.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                mainUI.pack();
                mainUI.setResizable(false);
                mainUI.setVisible(true);
                mainUI.setAlwaysOnTop(true);
                MidiFile midi = null;
                try {
                    midi = new MidiFile(new File(midiFilePath));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.exit(-1);
                }

                MidiProcessor processor = new MidiProcessor(midi);
                processor.registerEventListener(this, MidiEvent.class);
                processor.start();

            }
        });
        fileChooser.showOpenDialog(null);


    }
    public static void main(String[] args)
            throws Exception {
        new Main();
//        JFrame frame = new JFrame("MidiPlayer");
//        frame.setLayout(new GridLayout(5,4));
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        Soundbank soundbank = MidiSystem.getSoundbank(new File("D:\\FluidR3 GM2-2.SF2"));
//        Synthesizer synthesizer = new SoftSynthesizer();
//        synthesizer.open();
//        synthesizer.loadAllInstruments(soundbank) ;
//        JButton restore = new JButton("Default") ;
//        restore.addActionListener(e->{ channel = -1 ;});
//        frame.add(restore);
//        for(int i = 0 ; i < synthesizer.getChannels().length ; ++i){
////            System.out.println("length:"+synthesizer.getChannels().length);
//            synthesizer.getChannels()[i].programChange(i);
//            JButton button = new JButton("Channel :" + i) ;
//            int cur = i ;
//            button.addActionListener(e->{ channel = cur ;});
//            frame.add(button);
//
//        }
//
//        synthesizer.getChannels()[1].programChange(1);
//        receiver = synthesizer.getReceiver();
//

    }

    @Override
    public void onStart(boolean fromBeginning) {

    }

    @Override
    public void onEvent(MidiEvent event, long ms) {

        for(int i = 0 ; i < receivers.size() ;++i){
            receivers.get(i).dispatch(event, ms);
        }
    }

    @Override
    public void onStop(boolean finished) {

    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("select a sf2 file");
        fileChooser.addActionListener(e -> {
            try {
                receivers.add(new ChannelSwitch(fileChooser.getSelectedFile().getAbsolutePath()));
            } catch (MidiUnavailableException ex) {
                ex.printStackTrace();
            } catch (InvalidMidiDataException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        fileChooser.showOpenDialog(null);
    }
}