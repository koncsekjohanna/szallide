/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Handler;

import Protokoll.Message;
import java.io.IOException;

/**
 *
 * @author Johika
 */
public class MenuManager
{

    DataSet.Szerepkorok.Szerepkor szerepkor;

    public MenuManager(DataSet.Szerepkorok.Szerepkor szerepkor)
    {
        this.szerepkor = szerepkor;
    }

    public Message Handle(Message m)
    {
        try
        {
            switch (szerepkor)
            {
                case admin:

                    break;
                case karbantarto:
                    KarbantartoMenu karbantartoMenu = new KarbantartoMenu(m);
                    karbantartoMenu.run();
                    break;
                case recepcios:
                    RecepciosMenu recepciosMenu = new RecepciosMenu(m);
                    return recepciosMenu.run();
                case takarito:
                    TakaritoMenu takaritoMenu = new TakaritoMenu(m);
                    takaritoMenu.run();
                    break;
                case vendeg:
                    VendegMenu vendegMenu = new VendegMenu(m);
                    return vendegMenu.Run();
                case vezeto:

                    break;

                default:

                    break;
            }
        }
        catch (IOException exc)
        {

        }
        return m;
    }
}
