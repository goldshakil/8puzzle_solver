
//Fixed version
import java.util.LinkedList;
import java.util.Queue;
import java.util.Arrays;
import java.util.Stack;
import java.util.Map;
import java.io.*;
import java.util.*;
public class PuzzleSolver
{
  //the combination will create an down/up/right/left sequence
  static int move_x[]={1,-1,0,0};
  static int move_y[]={0,0,1,-1};
  static int path_length=0;

  public static void main(String[] args)
  {
    //please uncomment the puzzle you want to solve

   int puzzle[][]={{1,2,3},{0,5,6},{4,7,8}}; //Test Instance
  //int puzzle[][]={{4,1,0},{6,3,2},{7,5,8}}; //P1
   //int puzzle[][]={{4,3,1},{6,5,2},{0,7,8}}; //P2
    //int puzzle[][]={{3,1,0},{4,5,2},{6,7,8}}; //P3
  // int puzzle[][]={{0,1,2},{3,4,5},{6,7,8}}; //P4

    //find the x,y coordinates for the blank tile
    int i=0,j=0;
    outer_loop:
    for(i=0;i<3;i++)
    {
      for(j=0;j<3;j++)
      {
        if(puzzle[i][j]==0)
        {
          break outer_loop;
        }
      }
    }

    //check solvability
    if(puzzle_checker(puzzle))
    {
      //please uncomment the method you want to use
    // BFS_solve(puzzle,i,j);
     DFS_solve(puzzle,i,j);
    // A1_star(puzzle,i,j); //#tiles out of place
     //A2_star(puzzle,i,j); //Manhattan Distance
    }
    else
    {
      System.out.println("Unsolvable Puzzle");
    }
  }//end of main loop

  //A2 star -> Manhattan Distance
  static void A2_star(int puzzle[][],int x,int y)
  {
    Vector<node> A_vector=new Vector<node>(); //priority queue can be used as an open list
    Vector<node> visited=new Vector<node>(); // Closed List

    //push root node
    node root=new node();
    root.setter(puzzle,x,y,0,0,null,'N');
    root.cost=calculate_manhattan(root.grid); //heuristic cost -> calculate
    root.old_cost=0; //current cost=0
    A_vector.addElement(root);//adding the root node

    int visited_nodes=0;
    int stack_size=0;

    while(true)
    {
      if(stack_size<A_vector.size())//vector size increased -> update the maximum size
      {
        stack_size=A_vector.size();
      }
      if(A_vector.isEmpty())//if Queue is empty break
      {
        System.out.println("Failed to Find a Solution");
        break;
      }
      else //visiting nodes
      {
        node temp=new node();
        while(true) //visit the right v
        {
          //remove the element with lowest cost
          int lowet_cost_index=remove_min(A_vector);
          temp=A_vector.get(lowet_cost_index);//temp is lowest cost element in open
          A_vector.remove(lowet_cost_index);

          if(!contains_same_array(temp,visited))//this state hasn't been reached -> not in closed
          {
            break;
          }
          else //a matching array is found
          {
            int matching_index=get_matching_index(temp,visited);
            if(visited.get(matching_index).cost>temp.cost)//check new cost (temp.cost)
            {
              visited.remove(matching_index);
              break;
            }
          }
        }
        //adding v to closed (mark it as visited)
        visited.addElement(temp);

        visited_nodes++;

        if(temp.goal_checker()) //Reached the goal state
        {
          print_pathToparent(temp); //print path from to goal to parent
          System.out.println("Path Length is: "+path_length);
          System.out.println("Visited Nodes are: "+visited_nodes);
          System.out.println("Open Size is: "+stack_size);
          break;
        }
        else //not a goal state -> expand and children to the queue
        {
          for(int i=0;i<4;i++)//check 4 directions
          {
            if((temp.blank_x)+move_x[i]<3&&(temp.blank_y)+move_y[i]<3&&(temp.blank_x)+move_x[i]>=0&&(temp.blank_y)+move_y[i]>=0)//can be moved-> possible child
            {
              node new_child=new node();//new_possible child
              new_child.setter(temp.grid,temp.blank_x,temp.blank_y,temp.blank_x+move_x[i],temp.blank_y+move_y[i],temp,'Y');//passing new blank coordinates
              new_child.cost=calculate_manhattan(new_child.grid)+new_child.parent.old_cost+1;
              new_child.old_cost=new_child.parent.old_cost+1;

              if(check_state(new_child)) //new child's state is totally new and didn't occur before
              {
                A_vector.addElement(new_child);
              }
              //add all possible children
            }
          }//end of for loop
        }
      }
    }
  }//end of A2star


  //A1 star

  static void A1_star(int puzzle[][],int x,int y)
  {
    Vector<node> A_vector=new Vector<node>(); //priority queue can be used //open
    Vector<node> visited=new Vector<node>();
    //push root node
    node root=new node();
    root.setter(puzzle,x,y,0,0,null,'N');
    root.cost=calculate_tiles(root.grid); //current cost =0 //heuristic cost -> calculate
    root.old_cost=0;
    A_vector.addElement(root);//adding the root node

    int visited_nodes=0;
    int stack_size=0;

    while(true)
    {
      if(stack_size<A_vector.size())//queue size increased -> update the maximum size
      {
        stack_size=A_vector.size();
      }
      if(A_vector.isEmpty())//if Queue is empty break
      {
        System.out.println("Failed to Find a Solution");
        break;
      }
      else //visiting nodes
      {
        node temp=new node();
        while(true) //visit the right v
        {
          //remove the element with lowest cost
          int lowet_cost_index=remove_min(A_vector);
          temp=A_vector.get(lowet_cost_index);//temp is lowest cost element in open
          A_vector.remove(lowet_cost_index);

          if(!contains_same_array(temp,visited))//this state hasn't been reached
          {
            break;
          }
          else //a matching array is found
          {
            int matching_index=get_matching_index(temp,visited);
            if(visited.get(matching_index).cost>temp.cost)//check new cost
            {
              visited.remove(matching_index);
              break;
            }
          }
          //even with a matching array if the cost is higher we skip it
        }
        //adding v to closed (mark it as visited)
        visited.addElement(temp);

        visited_nodes++;//not sure

        if(temp.goal_checker()) //Reached the goal state
        {
          print_pathToparent(temp); //print path from to goal to parent
          System.out.println("Path Length is: "+path_length);
          System.out.println("Visited Nodes are: "+visited_nodes);
          System.out.println("Open Size is: "+stack_size);
          break;
        }
        else //not a goal state -> expand and children to the queue
        {
          for(int i=0;i<4;i++)//check 4 directions
          {
            if((temp.blank_x)+move_x[i]<3&&(temp.blank_y)+move_y[i]<3&&(temp.blank_x)+move_x[i]>=0&&(temp.blank_y)+move_y[i]>=0)//can be moved-> possible child
            {
              node new_child=new node();//new_possible child
              new_child.setter(temp.grid,temp.blank_x,temp.blank_y,temp.blank_x+move_x[i],temp.blank_y+move_y[i],temp,'Y');//passing new blank coordinates
              new_child.cost=calculate_tiles(new_child.grid)+new_child.parent.old_cost+1;
              new_child.old_cost=new_child.parent.old_cost+1;

              if(check_state(new_child)) //new child's state is totally new and didn't occur before
              {
                A_vector.addElement(new_child);
              }
              //add all possible children
            }
          }//end of for
        }
      }
    }
  }//end of A1star



  //DFS solver
  static void DFS_solve(int puzzle[][],int x,int y)
  {
    Stack<node> DFS_stack=new Stack<node>();
    //push root node
    node root=new node();
    root.setter(puzzle,x,y,0,0,null,'N');
    DFS_stack.push(root);//adding the root node

    int visited_nodes=0;
    int stack_size=0;


    while(true)
    {
      if(stack_size<DFS_stack.size())//queue size increased -> update the maximum size
      {
        stack_size=DFS_stack.size();
      }
      if(DFS_stack.isEmpty())//if Queue is empty break
      {
        System.out.println("Failed to Find a Solution");
        break;
      }
      else //visiting nodes
      {
        visited_nodes++;
        node temp=DFS_stack.pop();// deqeue

        if(temp.goal_checker()) //Reached the goal state
        {
          print_pathToparent(temp); //print path from to goal to parent
          System.out.println("Path Length is: "+path_length);
          System.out.println("Visited Nodes are: "+visited_nodes);
          System.out.println("Open Size is: "+stack_size);
          break;
        }
        else //not a goal state -> expand and children to the queue
        {
          for(int i=0;i<4;i++)//check 4 directions
          {
            if((temp.blank_x)+move_x[i]<3&&(temp.blank_y)+move_y[i]<3&&(temp.blank_x)+move_x[i]>=0&&(temp.blank_y)+move_y[i]>=0)//can be moved-> possible child
            {
              node new_child=new node();//new_possible child
              new_child.setter(temp.grid,temp.blank_x,temp.blank_y,temp.blank_x+move_x[i],temp.blank_y+move_y[i],temp,'Y');//passing new blank coordinates

              if(check_state(new_child)) //new child's state is totally new and didn't occur before
              {
                DFS_stack.push(new_child);
              }
              //add all possible children
            }
          }//end of for
        }
      }
    }
  }//end of DFS


  //BFS solver
  static void BFS_solve(int puzzle[][],int x,int y)
  {
    Queue<node> BFS_queue=new LinkedList<node>();
    //push root node
    node root=new node();
    root.setter(puzzle,x,y,0,0,null,'N');
    BFS_queue.add(root);//adding the root node

    int visited_nodes=0;
    int queue_size=0;


    while(true)
    {
      if(queue_size<BFS_queue.size())//queue size increased -> update the maximum size
      {
        queue_size=BFS_queue.size();
      }
      if(BFS_queue.isEmpty())//if Queue is empty break
      {
        System.out.println("Failed to Find a Solution");
        break;
      }
      else //visiting nodes
      {
        visited_nodes++;
        node temp=BFS_queue.poll();// deqeue

        if(temp.goal_checker()) //Reached the goal state
        {
          print_pathToparent(temp); //print path from to goal to parent
          System.out.println("Path Length is: "+path_length);
          System.out.println("Visited Nodes are: "+visited_nodes);
          System.out.println("Open Size is: "+queue_size);
          break;
        }
        else //not a goal state -> expand and children to the queue
        {
          for(int i=0;i<4;i++)//check 4 directions
          {
            if((temp.blank_x)+move_x[i]<3&&(temp.blank_y)+move_y[i]<3&&(temp.blank_x)+move_x[i]>=0&&(temp.blank_y)+move_y[i]>=0)//can be moved-> possible child
            {
              node new_child=new node();//new_possible child
              new_child.setter(temp.grid,temp.blank_x,temp.blank_y,temp.blank_x+move_x[i],temp.blank_y+move_y[i],temp,'Y');//passing new blank coordinates

              if(check_state(new_child)) //new child's state is totally new and didn't occur before
              {
                BFS_queue.add(new_child);
              }
              //add all possible children
            }

          }//end of for
        }
        }
      }
    }//end of BFS

//check_state Function to check whether this state has occured until the parent node
static boolean check_state(node temp) // true can be added //false it has a repretition-> don't add
{
  //temp is a possible child to be added
  boolean checker_bool=true;
  node father=temp.parent;// keep on update father
  if(father==null) //no matching since this is root node
  {
    return checker_bool;
  }
  else
  {
    while(true)
    {
      if(father==null) //reached father and checker all
      {
        break;
      }
      if(array_comparer(father.grid,temp.grid))//arrays are equal don't add
      {
        checker_bool=false;
        break;
      }

      father=father.parent;//check the upper level
    }
  }
  return checker_bool;
}
  //Function to check whether this state has been reached with another cost
  static boolean contains_same_array(node temp, Vector<node>visited)
  {
    //temp is the new element
    for(int i=0;i<visited.size();i++)//comapre temp too all nodes in visited
    {
      if(array_comparer(temp.grid,visited.get(i).grid)==true)//if there is matching array and new cost is lower
      {
        return true;//found such element
      }
    }
    return false;

  }
  //get the index of the  matching state
  static int get_matching_index(node temp, Vector<node>visited)
  {
    //temp is the new element
    for(int i=0;i<visited.size();i++)//comapre temp too all nodes in visited
    {
      if(array_comparer(temp.grid,visited.get(i).grid)==true)//if there is matching array and new cost is lower
      {
        return i;//found such element
      }
    }
    return 0;

  }
  //follow up function to check the equality of two Arrays
  static boolean array_comparer(int new_a[][], int old[][])
  {

    for(int i=0;i<3;i++)
    {
      for(int j=0;j<3;j++)
      {
        if(new_a[i][j]!=old[i][j]) //arrays are not equal
        {
          return false;

        }
      }

    }
    return true;//arrays are equal

  }



  //Function to remove the element with min cost from the open list for A* -> returns its index
  static int remove_min(Vector<node> A_vector)
  {
    int min_element=0; //suppose the first element has the lowest cost
    for(int i=0;i<A_vector.size();i++)
    {
      if(A_vector.get(min_element).cost>A_vector.get(i).cost) //compare to other elements in the vector
      {
        min_element=i;
      }
    }
    return min_element; //return index of element with lowrst cost
  }

  //first heuristic function to check number of tiles out of order
  static int calculate_tiles(int puzzle[][])
  {
    int goal_grid[][]={{1,2,3},{4,5,6},{7,8,0}};
    int out_place=0;
    for(int i=0;i<3;i++)
    {
      for(int j=0;j<3;j++)
      {
        if(puzzle[i][j]!=goal_grid[i][j] &&puzzle[i][j]!=0)out_place++;
      }

    }
    return out_place;
  }

  //second Heuristic function to check the manhattan distance
  static int calculate_manhattan(int puzzle[][])
  {
    int goal_grid[][]={{1,2,3},{4,5,6},{7,8,0}};
    int out_place=0;
    int sum=0;
    for(int i=0;i<3;i++) //row
    {
      for(int j=0;j<3;j++)
      {
        if(puzzle[i][j]!=goal_grid[i][j] &&puzzle[i][j]!=0)//calculate distance if the tile is out of place
        {
          if(puzzle[i][j]==1)
          {
            sum=sum+Math.abs(0-i)+Math.abs(0-j);
          }
          else if(puzzle[i][j]==2)
          {
            sum=sum+Math.abs(0-i)+Math.abs(1-j);
          }
          else if(puzzle[i][j]==3)
          {
            sum=sum+Math.abs(0-i)+Math.abs(2-j);
          }
          else if(puzzle[i][j]==4)
          {
            sum=sum+Math.abs(1-i)+Math.abs(0-j);
          }
          else if(puzzle[i][j]==5)
          {
            sum=sum+Math.abs(1-i)+Math.abs(1-j);
          }
          else if(puzzle[i][j]==6)
          {
            sum=sum+Math.abs(1-i)+Math.abs(2-j);
          }
          else if(puzzle[i][j]==7)
          {
            sum=sum+Math.abs(2-i)+Math.abs(0-j);
          }
          else if(puzzle[i][j]==8)
          {
            sum=sum+Math.abs(2-i)+Math.abs(1-j);
          }
        }

      }
    }
    return sum;
  }


  // a function to check whether the direction is up down right or left
  static char direction_checker(int x,int y)
  {
    if(x==1&&y==0)
    {
      return 'D';
    }
    else if(x==-1&&y==0)
    {
      return 'U';
    }
    else if(x==0&&y==1)
    {
      return 'R';
    }
    else
    {
      return 'L';
    }
  }


  // A function to check the solvaility of a specific instance of a 8-puzzle
  static boolean puzzle_checker(int puzzle[][])
  {
    int new_array[]=new int[9];
    int c=0;
    //copy all elements in 1-D array
    for(int i=0;i<3;i++)
    {
      for(int j=0;j<3;j++)
      {
        new_array[c]=puzzle[i][j];
        c++;
      }
    }
    //check the number of inversions in the 1-D array (compare each element to all next ones)
    int counter=0;
    for(int i=0;i<9;i++)
    {
      for(int j=i+1;j<9;j++)
      {
        if((new_array[i]!=0)&&(new_array[j]!=0)&&(new_array[i]>new_array[j]))
        {
          counter++;
        }
      }
    }
    if(counter%2==0)
    {
      return true;
    }
    else
    {
      return false;
    }
  }//end of puzzle_checker

  //function to print the chain of the path
  static void print_pathToparent(node my_node)
  {
    my_node.print_array();
    System.out.println("  ^");
    System.out.println("  |");
    if(my_node.parent!=null)
    {
      path_length++;
      print_pathToparent(my_node.parent);
    }

  }

}//end of the public class

//node class
class node
{
  node parent;
  int grid[][]=new int[3][3] ;
  int blank_x;
  int blank_y;
  char last_move;//check last move R L U P to prevent creating an indentical parent/child nodes
  int cost; //sum of old and future
  int old_cost;
  void setter(int puzzle[][],int blank_x_value,int blank_y_value,int new_x,int new_y,node parent_node,char move)
  {
    for(int i=0;i<3;i++) //copying the puzzle
    {
      for(int j=0;j<3;j++)
      {
        this.grid[i][j]=puzzle[i][j];
      }
    }

    this.blank_x=blank_x_value;
    this.blank_y=blank_y_value;
    this.parent=parent_node;
    this.last_move=move;
    if(this.last_move!='N')//some value should be swapped for children nodes generation
    {
      int temp=this.grid[blank_x_value][blank_y_value]; //original location of 0
      this.grid[blank_x_value][blank_y_value]=this.grid[new_x][new_y];
      this.grid[new_x][new_y]=temp;//new location of zero
      //update the blank coordinates
      this.blank_x=new_x;
      this.blank_y=new_y;
    }
    else //for parent
    {
      this.blank_x=blank_x_value;
      this.blank_y=blank_y_value;
    }
  }

  void print_array()
  {
    for(int i=0;i<3;i++)
    {
      for(int j=0;j<3;j++)
      {
        System.out.printf("%d ",grid[i][j]);
      }
      System.out.println();
    }
  }


  boolean goal_checker()//compare to the final answer
  {
    int goal[][]={{1,2,3},{4,5,6},{7,8,0}};
    for(int i=0;i<3;i++)
    {
      if(!Arrays.equals(goal[i], this.grid[i]))
      {
        return false;
      }
    }
    return true;
  }
}
