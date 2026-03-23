import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { HealthModule } from './health/health.module';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      envFilePath: '../../.env',
    }),
    HealthModule,
    // Phase 1: BusinessModule, StaffModule, ServiceModule, SlotModule
    // Phase 2: BookingModule
    // Phase 3: WhatsAppModule
  ],
})
export class AppModule {}
