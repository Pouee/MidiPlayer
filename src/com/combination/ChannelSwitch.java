package com.combination;

import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.sun.media.sound.SoftSynthesizer;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class ChannelSwitch {
    // -2 代表关闭声音 -1 代表默认
    private int channel = -2 ;
    private Receiver receiver ;
    private String soundFontPath ;
    public void dispatch(MidiEvent event, long ms){
        if(channel == -2) return;
        if (event.getClass() == NoteOn.class ) {
            NoteOn noteOn = ((NoteOn) event);
            try {
                ShortMessage msg = new ShortMessage();
                msg.setMessage(ShortMessage.NOTE_ON, channel == -1 ? noteOn.getChannel() : channel, noteOn.getNoteValue(), noteOn.getVelocity());
                receiver.send(msg, ms);
            } catch (InvalidMidiDataException e) {
                e.printStackTrace();
            }
        } else if (event.getClass() == NoteOff.class) {
            NoteOff noteOff = ((NoteOff) event);
            try {
                ShortMessage msg = new ShortMessage();
                msg.setMessage(ShortMessage.NOTE_OFF, channel == -1 ? noteOff.getChannel() : channel, noteOff.getNoteValue(), noteOff.getVelocity());
                receiver.send(msg, ms);
            } catch (InvalidMidiDataException e) {
                e.printStackTrace();
            }

        }
    }
    public ChannelSwitch(String soundFontPath) throws MidiUnavailableException, InvalidMidiDataException, IOException {
        this.soundFontPath = soundFontPath;
        JFrame frame = new JFrame(soundFontPath);
        frame.setLayout(new GridLayout(3,6));
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                channel = -2 ;
                System.out.println("DISPOSE");
                frame.dispose();
            }
        });
        Soundbank soundbank = MidiSystem.getSoundbank(new File(soundFontPath));
        Synthesizer synthesizer = new SoftSynthesizer();
        synthesizer.open();
        synthesizer.loadAllInstruments(soundbank) ;
        JButton restore = new JButton("Default") ;
        JButton mute = new JButton("Mute") ;
        restore.addActionListener(e->{ channel = -1 ;});
        mute.addActionListener(e->{ channel = -2 ;});
        frame.add(mute);
        frame.add(restore);
        for(int i = 0 ; i < synthesizer.getChannels().length ; ++i){
            synthesizer.getChannels()[i].programChange(i);
            JButton button = new JButton("Channel :" + i) ;
            int cur = i ;
            button.addActionListener(e->{ channel = cur ;});
            frame.add(button);
        }
        synthesizer.getChannels()[1].programChange(1);
        receiver = synthesizer.getReceiver();
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }
}