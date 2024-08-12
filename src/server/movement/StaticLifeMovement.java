/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server.movement;

import java.awt.Point;
import tools.data.MaplePacketLittleEndianWriter;

/**
 *
 * @author Itzik
 */
public class StaticLifeMovement extends AbstractLifeMovement {

    private Point pixelsPerSecond, offset;
    private short unk, fh;
    private int wui;

    public StaticLifeMovement(int type, Point position, int duration, int newstate, int newfh) {
        super(type, position, duration, newstate, newfh);
    }

    public void setPixelsPerSecond(Point wobble) {
        this.pixelsPerSecond = wobble;
    }

    public void setOffset(Point wobble) {
        this.offset = wobble;
    }

    public void setFh(short fh) {
        this.fh = fh;
    }

    public void setUnk(short unk) {
        this.unk = unk;
    }

    public short getUnk() {
        return unk;
    }

    public void setWui(int wui) {
        this.wui = wui;
    }

    public void defaulted() {
        unk = 0;
        fh = 0;
        pixelsPerSecond = new Point(0, 0);
        offset = new Point(0, 0);
        wui = 0;
    }

    @Override
    public void serialize(MaplePacketLittleEndianWriter lew) {
        lew.write(getType());
        switch (getType()) {
            case 0:
            case 7:
            case 14:
            case 16:
            case 50:
            case 51:
                lew.writePos(getPosition());
                lew.writePos(pixelsPerSecond);
                lew.writeShort(unk);
                if (getType() == 14) {
                    lew.writeShort(fh);
                }
                lew.writePos(offset);
                break;
            case 1:
            case 2:
            case 15:
            case 18:
            case 19:
            case 21:
            case 45:
            case 46:
            case 47:
            case 48:
                lew.writePos(pixelsPerSecond);
                if (getType() == 18 || getType() == 19) {
                    lew.writeShort(fh);
                }
                break;
            case 3:
            case 4:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 23:
            case 44:
                lew.writePos(getPosition());
                lew.writeShort(unk);
                break;
            case 13:
                lew.writePos(pixelsPerSecond);
                lew.writeShort(fh);
                break;
            case 20:
                lew.writePos(getPosition());
                lew.writePos(pixelsPerSecond);
                break;
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
                break;
            case 49:
                lew.writePos(getPosition());
                lew.writePos(pixelsPerSecond);
                lew.writeShort(unk);
                break;
        }
        if (getType() != 11) {
            lew.write(getNewstate());
            lew.writeShort(getDuration());
//            lew.write(wui);
        } else {
            lew.write(wui);
        }
    }
}
