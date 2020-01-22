//This is a program by Mia Paulin that demonstrates the use of locking
// signalAll and await methods and multiple threads
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.locks.*;
import java.awt.geom.*;
import java.util.*;



public class BounceThread2
{
	public static void main(String[] args)
	{
		BounceFrame frame=new BounceFrame();
		frame.setTitle("Bounce Thread");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}

class BounceFrame extends JFrame
{
		private Ball b=new Ball();
	    private BallComponent comp=new BallComponent();
	    private BallRunnable r=new BallRunnable(b, comp);
	  

	

	
	public BounceFrame()
	{
		this.add(comp, BorderLayout.CENTER);
		this.add(new SouthPanel(), BorderLayout.SOUTH);
		comp.add(b);
	}
	
	class SouthPanel extends JPanel
	{
		private JButton startButton=new JButton("Start");
		private JButton holdButton=new JButton("Hold On");
		private JButton keepButton=new JButton("Keep Going");
		private JButton closeButton=new JButton("Close");
				
		public SouthPanel()
		{
			
		
			ActionListener startListener=new ActionListener()
			{
				public void actionPerformed(ActionEvent Event)
				{
				 Thread t=new Thread(r);
					t.start();
					startButton.setEnabled(false);
				}
			};
			
			startButton.addActionListener(startListener);
			this.add(startButton);
			
			ActionListener holdListener=new ActionListener()
			{
				public void actionPerformed(ActionEvent Event)
				{
					r.requestSuspend();
				}
			};
			
			holdButton.addActionListener(holdListener);
			this.add(holdButton);
			
			ActionListener keepListener=new ActionListener()
			{
				public void actionPerformed(ActionEvent Event)
				{
					r.requestResume();
				}
			};
			
			keepButton.addActionListener(keepListener);
			this.add(keepButton);
			
			ActionListener closeListener=new ActionListener()
			{
				public void actionPerformed(ActionEvent Event)
				{
					System.exit(0);
				}
			};
			
			closeButton.addActionListener(closeListener);
			this.add(closeButton);
		}
	}
}

class BallRunnable implements Runnable
{
	private volatile Boolean suspendRequested=false;
	private Lock suspendLock=new ReentrantLock();
	private Condition suspendCondition=suspendLock.newCondition();
	private Ball myBall;
	private Component myComponent;
	
	public BallRunnable(Ball myBall, Component myComponent)
	{
		this.myBall=myBall;
		this.myComponent=myComponent;
	}
	
		public void requestSuspend()
	{
		suspendRequested=true;
	}	
	
	public void requestResume()
	{
		suspendRequested=false;
		suspendLock.lock();
		try{suspendCondition.signalAll();}
		finally{suspendLock.unlock();}
	}

	
	public void run()
	{
		
		try
		{
		for(int i=0;i<=5000;i++)
		 {
			myBall.move(myComponent.getBounds());
			myComponent.repaint();
			Thread.sleep(5);
			if(suspendRequested)
			{
				suspendLock.lock();
				try{while(suspendRequested) suspendCondition.await(); }
				finally{suspendLock.unlock();}
			}
		}
		}
		catch(InterruptedException e) {}

	}	
	}

 class BallComponent extends JComponent
{
   private static final int DEFAULT_WIDTH = 450;
   private static final int DEFAULT_HEIGHT = 350;

   private java.util.List<Ball> balls = new ArrayList<>();

     public void add(Ball b)
   {
      balls.add(b);
   }

   public void paintComponent(Graphics g)
   {
      Graphics2D g2 = (Graphics2D) g;
      for (Ball b : balls)
      {
         g2.fill(b.getShape());
      }
   }
   
   public Dimension getPreferredSize() { return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT); }
}
 class Ball
{
   private static final int XSIZE = 15;
   private static final int YSIZE = 15;
   private double x = 0;
   private double y = 0;
   private double dx = 1;
   private double dy = 1;

     public void move(Rectangle2D bounds)
   {
      x += dx;
      y += dy;
      if (x < bounds.getMinX())
      { 
         x = bounds.getMinX();
         dx = -dx;
      }
      if (x + XSIZE >= bounds.getMaxX())
      {
         x = bounds.getMaxX() - XSIZE; 
         dx = -dx; 
      }
      if (y < bounds.getMinY())
      {
         y = bounds.getMinY(); 
         dy = -dy;
      }
      if (y + YSIZE >= bounds.getMaxY())
      {
         y = bounds.getMaxY() - YSIZE;
         dy = -dy; 
      }
   }

  
   public Ellipse2D getShape()
   {
      return new Ellipse2D.Double(x, y, XSIZE, YSIZE);
   }
}
