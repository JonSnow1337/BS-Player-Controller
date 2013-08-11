import net.java.games.input.Controller.Type;

import java.awt.AWTException;
import java.awt.RenderingHints.Key;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
public class BsPlayerController 
{
	//tipka 1 je 6
	JInputJoystick gamepad = null;
	Robot keyboardOutput = null;
	public String path;
	public Timer loopTimer = new Timer(16, new Loop());
	Process bsPlayerProcess = null;
	JFrame helpFrame = new JFrame();
	public boolean controllerUsed = false;
	public BsPlayerController(String path) throws AWTException
	{
		
		this.path = path;
		ImageIcon helpIcon = new ImageIcon("images/controllermap.jpg");
		JLabel jlbl = new JLabel(helpIcon);
		helpFrame.add(jlbl);
		helpFrame.setUndecorated(true);
		helpFrame.setSize(1980, 1080);
		helpFrame.setVisible(false);
		
		helpFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gamepad = new JInputJoystick(Type.GAMEPAD, Type.STICK);
		keyboardOutput = new Robot();
		runBsPlayer();
		loopTimer.start();
	}
	public static void main(String[] args) throws AWTException, IOException
	{
		JFileChooser jfl = new JFileChooser();
	
		String path = null;
		File bsPath = new File("path.txt");
		
		if(!bsPath.exists())
		{
			JOptionPane.showMessageDialog(null, "Find the path to bsPlayer.exe\n" +
					"You only have to do this once");
		
			if(jfl.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				path  = jfl.getSelectedFile().getAbsolutePath();
				bsPath.createNewFile();
				PrintWriter output = new PrintWriter(bsPath);
				output.write(path);
				output.close();
				
			}
		}
		else
		{
			Scanner input = new Scanner(bsPath);
			path = input.nextLine();
			input.close();
			
		}
		
			
		BsPlayerController bsc = new BsPlayerController(path);
		
			
		
	}
	public void runBsPlayer()  
	{
		try
		{
			 
			bsPlayerProcess = Runtime.getRuntime().exec(path);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public void quitBsPlayer()
	{
		try
		{
			Runtime.getRuntime().exec("tsKill bsplayer");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	

	

	public void useHatSwitch()
	{
		float hatSwitchPos = gamepad.getHatSwitchPosition();
		if(Float.compare(hatSwitchPos, net.java.games.input.Component.POV.UP) == 0)
		{
			keyboardOutput.keyPress(KeyEvent.VK_UP);
		}
		else if(Float.compare(hatSwitchPos, net.java.games.input.Component.POV.DOWN) == 0)
		{
			keyboardOutput.keyPress(KeyEvent.VK_DOWN);
		}
		else if(Float.compare(hatSwitchPos, net.java.games.input.Component.POV.LEFT) == 0)
		{
			for(int i= 0; i < 1000; i++)
			{
				keyboardOutput.keyPress(KeyEvent.VK_LEFT);
			}
		}
		else if(Float.compare(hatSwitchPos, net.java.games.input.Component.POV.RIGHT) == 0)
		{
			for(int i = 0; i < 1000; i++)
			{
				keyboardOutput.keyPress(KeyEvent.VK_RIGHT);
			}
		}
	}
	
	public int getButtonHit()
	{
		gamepad.pollController();
		
		for(int i = 0; i < gamepad.getNumberOfButtons(); i++ )
		{
			if(gamepad.getButtonValue(i))
			{
				System.out.println(i);
				return i;
				
			}
		
		}
		return -1;
		
	}
	public void useButtons()
	{
		int buttonHit = getButtonHit();
		switch(buttonHit)
		{                      
		case 7:
			keyboardOutput.keyPress(KeyEvent.VK_B);
			keyboardOutput.keyRelease(KeyEvent.VK_B);

			break;
		case 8:
			keyboardOutput.keyPress(KeyEvent.VK_SPACE);
			keyboardOutput.keyRelease(KeyEvent.VK_SPACE);
			break;
		case 9:
			keyboardOutput.keyPress(KeyEvent.VK_Y);
			keyboardOutput.keyRelease(KeyEvent.VK_Y);

			break;

		case 10:
			keyboardOutput.keyPress(KeyEvent.VK_F);
			keyboardOutput.keyRelease(KeyEvent.VK_F);
			break;
		case 11:
			quitBsPlayer();
			System.exit(0);
			break;
		case 14:
			if(helpFrame.isVisible())
			{
				helpFrame.setVisible(false);
			}
			else
			{
				helpFrame.setVisible(true);
			}
			break;
		}
		
	}
	public void useStick()
	{
		if(gamepad.getX_LeftJoystick_Percentage() <= 10)
		{
			keyboardOutput.keyPress(KeyEvent.VK_LEFT);
			
		}
		if(gamepad.getX_LeftJoystick_Percentage() >= 90)
		{
			keyboardOutput.keyPress(KeyEvent.VK_RIGHT);
			
		}
		
		
	}
	
	class Loop implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(!controllerUsed)
			{
				gamepad.pollController();
				useHatSwitch();
				useButtons();
			}
			controllerUsed = gamepad.controllerUsed();
		
			useStick();
		}
		
	}
	
}
