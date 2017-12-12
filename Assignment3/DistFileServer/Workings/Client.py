import random
import dfs.api

if __name__ == '__main__':

    # File server
    #requesting fies
    file_1 = dfs.api.open('/etc/blub', 'w')
    file_1.write(str(random.randint(0, 10000)) * 10)
    file_1.close()

    file_2 = dfs.api.open('/etc/blub', 'r')
    read_content = file_2.read()
    print("read content = ", read_content)
    file_2.close()



    # fs1
    file_1 = dfs.api.open('/home/my_file2', 'w')
    file_1.write(str(random.randint(0, 10000)) * 10)
    file_1.close()

    file_2 = dfs.api.open('/home/my_file2', 'r')
    read_content = f2.read()
    print("read content = ", read_content)
    file_2.close()