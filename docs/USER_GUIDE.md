# User Guide - BarberShop

## 📋 Table of Contents
- [Getting Started](#getting-started)
- [Customer Guide](#customer-guide)
- [Barber Guide](#barber-guide)
- [Administrator Guide](#administrator-guide)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)

## 🚀 Getting Started

### System Requirements
- **Web Browser**: Chrome, Firefox, Safari, or Edge (latest versions)
- **Internet Connection**: Required for accessing the application
- **JavaScript**: Must be enabled in your browser
- **Cookies**: Must be enabled for session management

### Accessing the Application
1. Open your web browser
2. Navigate to the application URL (e.g., `https://your-barbershop.com`)
3. The homepage will display available barbers and booking options

## 👥 Customer Guide

### Browsing Barbers

#### Viewing Available Barbers
1. **Homepage**: The main page shows all available barbers
2. **Branch Filter**: Use the branch dropdown to filter barbers by location
3. **Barber Cards**: Each barber is displayed with:
   - Profile photo
   - Name
   - Branch location
   - Specialization information

#### Understanding Barber Information
- **Photo**: Professional headshot of the barber
- **Name**: Barber's full name
- **Branch**: Location where the barber works
- **Information**: Specializations, experience, or special notes

### Booking an Appointment

#### Step-by-Step Booking Process

1. **Select a Barber**
   - Browse through available barbers
   - Click on a barber card to view details
   - Click "Book Appointment" button

2. **Fill Booking Form**
   - **Name**: Enter your full name
   - **Phone**: Provide your contact number
   - **Date & Time**: Select preferred appointment slot
   - **Message**: Add any special requests or notes (optional)

3. **Submit Booking**
   - Review your information
   - Click "Book Appointment" to confirm
   - You'll see a success message if booking is confirmed

#### Date and Time Selection
- **Date Picker**: Click the date field to open a calendar
- **Time Slots**: Available times are shown in 30-minute intervals
- **Availability**: Grayed-out times indicate unavailable slots
- **Business Hours**: Appointments are available during business hours

#### Booking Confirmation
- **Success Message**: Green notification appears after successful booking
- **Confirmation**: Your appointment is automatically saved
- **No Email**: Currently, no confirmation email is sent

### Managing Your Booking

#### Viewing Your Appointment
- Contact the barbershop directly to check your appointment
- Barbers can view and manage appointments through their dashboard

#### Canceling or Rescheduling
- Contact the barbershop directly to modify your appointment
- Barbers can edit appointments through their dashboard

## 💇 Barber Guide

### Accessing Your Dashboard

#### Login Process
1. **Navigate to Login**: Click "Login" or go to `/login`
2. **Enter Credentials**:
   - **Username**: Your email address
   - **Password**: Your assigned password
3. **Submit**: Click "Login" button
4. **Dashboard Access**: You'll be redirected to your dashboard

#### Dashboard Overview
Your dashboard displays:
- **Personal Information**: Your name and branch
- **Today's Appointments**: Current day's schedule
- **Upcoming Appointments**: Future appointments
- **Quick Actions**: Edit and delete options

### Managing Appointments

#### Viewing Appointments
- **Daily View**: See appointments for the current day
- **Chronological Order**: Appointments are sorted by time
- **Customer Details**: Name, phone, and comments for each appointment

#### Editing Appointments
1. **Select Appointment**: Click "Edit" next to an appointment
2. **Modify Details**:
   - **Customer Name**: Update if needed
   - **Phone Number**: Update contact information
   - **Date & Time**: Reschedule appointment
   - **Comments**: Add or modify notes
3. **Save Changes**: Click "Save" to update

#### Deleting Appointments
1. **Select Appointment**: Click "Delete" next to an appointment
2. **Confirmation**: Confirm deletion
3. **Removal**: Appointment is permanently deleted

#### Adding Notes
- **Comments Field**: Add notes about customer preferences
- **Special Requests**: Document any special requirements
- **Follow-up Notes**: Track customer history

### Profile Management

#### Viewing Your Profile
- **Dashboard Header**: Shows your name and branch
- **Profile Information**: Displayed in the admin panel (if you have access)

#### Updating Information
- Contact your administrator to update:
  - Profile photo
  - Personal information
  - Branch assignment
  - Contact details

## 👨‍💼 Administrator Guide

### Accessing Admin Panel

#### Login Requirements
- **Admin Role**: Must have administrator privileges
- **Credentials**: Use your admin username and password
- **Access**: Navigate to `/admin` after login

#### Admin Dashboard
The admin panel provides:
- **User Management**: Manage barber accounts
- **Appointment Overview**: View all appointments
- **System Settings**: Configure branches and settings
- **File Management**: Handle barber photos

### Managing Barbers

#### Adding New Barbers

1. **Navigate to Add Barber Section**
   - Click "Add New Barber" button
   - Fill in the required information

2. **Barber Information**
   - **Name**: Full name of the barber
   - **Photo**: Upload professional headshot
   - **Branch**: Select branch location
   - **Information**: Add specializations or notes

3. **Account Creation**
   - **Email**: Create login username (email format)
   - **Password**: Set initial password
   - **Role**: Assign BARBER or ADMIN role

4. **Save Barber**
   - Click "Add Barber" to create account
   - Barber can immediately log in with provided credentials

#### Editing Existing Barbers

1. **Select Barber**
   - Click "Edit" next to barber name
   - Or use the edit form in admin panel

2. **Modify Information**
   - **Personal Details**: Update name, branch, information
   - **Photo**: Upload new profile picture
   - **Account Details**: Update email or password

3. **Save Changes**
   - Click "Save" to update information
   - Changes take effect immediately

#### Deleting Barbers

1. **Select Barber**
   - Click "Delete" next to barber name
   - Confirm deletion action

2. **Deletion Process**
   - Barber account is permanently removed
   - Associated appointments are deleted
   - Profile photo is removed from storage

### Managing Appointments

#### Viewing All Appointments
- **Admin Dashboard**: Shows appointments for all barbers
- **Filter Options**: Filter by barber or date
- **Detailed View**: Customer information and notes

#### Appointment Management
- **Edit Appointments**: Modify any appointment details
- **Delete Appointments**: Remove appointments as needed
- **Bulk Operations**: Manage multiple appointments

### System Configuration

#### Branch Management
- **Current Branches**: Shumen, Sofia, Plovdiv
- **Adding Branches**: Modify application configuration
- **Branch Assignment**: Assign barbers to specific branches

#### File Management
- **Photo Uploads**: Manage barber profile pictures
- **Storage**: Files stored in `/uploads/` directory
- **File Types**: JPG, PNG, GIF supported
- **Size Limits**: Maximum 10MB per file

#### User Roles
- **ADMIN**: Full system access
- **BARBER**: Dashboard and appointment management
- **Role Assignment**: Set during user creation

### Security Management

#### Password Policies
- **Encryption**: Passwords stored with BCrypt encryption
- **Reset Process**: Contact system administrator
- **Security**: Regular password updates recommended

#### Access Control
- **Role-Based Access**: Different permissions for different roles
- **Session Management**: Automatic logout after inactivity
- **Secure Login**: HTTPS encryption for all communications

## 🔧 Troubleshooting

### Common Issues

#### Login Problems
**Issue**: Cannot log in with correct credentials
**Solutions**:
- Check username/email spelling
- Verify password is correct
- Clear browser cookies and cache
- Try different browser
- Contact administrator if problem persists

#### Booking Issues
**Issue**: Cannot book appointment
**Solutions**:
- Check if date/time is available
- Ensure all required fields are filled
- Try selecting different time slot
- Refresh page and try again

#### File Upload Problems
**Issue**: Cannot upload barber photo
**Solutions**:
- Check file size (max 10MB)
- Verify file format (JPG, PNG, GIF)
- Ensure stable internet connection
- Try different file

#### Dashboard Loading Issues
**Issue**: Dashboard not loading properly
**Solutions**:
- Refresh the page
- Clear browser cache
- Check internet connection
- Try different browser
- Contact administrator

### Browser Compatibility

#### Supported Browsers
- **Chrome**: Version 90+
- **Firefox**: Version 88+
- **Safari**: Version 14+
- **Edge**: Version 90+

#### Browser Settings
- **JavaScript**: Must be enabled
- **Cookies**: Must be enabled
- **Pop-ups**: Allow for date picker
- **Cache**: Clear if experiencing issues

### Performance Tips

#### For Customers
- Use modern browser for best experience
- Ensure stable internet connection
- Book appointments during off-peak hours
- Have customer information ready

#### For Barbers
- Log out when finished using dashboard
- Regularly clear browser cache
- Use desktop browser for management tasks
- Keep appointment notes concise

#### For Administrators
- Use desktop browser for admin tasks
- Regularly backup important data
- Monitor system performance
- Keep user accounts updated

## ❓ FAQ

### General Questions

**Q: How do I book an appointment?**
A: Visit the homepage, select a barber, fill in your details, choose date/time, and submit the booking form.

**Q: Can I cancel my appointment?**
A: Contact the barbershop directly or ask your barber to cancel through their dashboard.

**Q: What if I'm running late?**
A: Contact the barbershop as soon as possible to inform them of your delay.

**Q: Can I reschedule my appointment?**
A: Yes, contact the barbershop or ask your barber to reschedule through their dashboard.

### Barber Questions

**Q: How do I access my appointments?**
A: Log in with your credentials and access your dashboard to view all appointments.

**Q: Can I edit customer information?**
A: Yes, you can edit appointment details including customer name, phone, and comments.

**Q: How do I add notes to appointments?**
A: Use the comments field when editing appointments to add notes about customer preferences.

**Q: What if I need to cancel an appointment?**
A: Use the delete function in your dashboard to remove appointments.

### Administrator Questions

**Q: How do I add a new barber?**
A: Use the "Add New Barber" function in the admin panel to create new barber accounts.

**Q: Can I change barber roles?**
A: Yes, you can edit barber accounts to change their role from BARBER to ADMIN or vice versa.

**Q: How do I manage file uploads?**
A: Barber photos are automatically managed when you upload them through the admin panel.

**Q: What if a barber forgets their password?**
A: You can reset their password by editing their account in the admin panel.

### Technical Questions

**Q: What browsers are supported?**
A: Modern browsers including Chrome, Firefox, Safari, and Edge are supported.

**Q: Is my data secure?**
A: Yes, the application uses secure authentication and data encryption.

**Q: Can I access the system from mobile?**
A: Yes, the application is responsive and works on mobile devices.

**Q: What if the system is down?**
A: Contact your system administrator for technical support.

---

This user guide provides comprehensive instructions for using the BarberShop application. For additional support, contact your system administrator or refer to the technical documentation. 